package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ReviewDBStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (:content, :is_positive, :user_id, :film_id) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("content", review.getContent());
        map.addValue("is_positive", review.getIsPositive());
        map.addValue("user_id", review.getUserId());
        map.addValue("film_id", review.getFilmId());

        jdbcOperations.update(sql, map, keyHolder);
        int reviewId = keyHolder.getKey().intValue();

        review.setReviewId(reviewId);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review removeReviewById(int id) {
        Review toReturn = getReviewById(id);
        String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return toReturn;
    }

    @Override
    public Review getReviewById(int id) {
        String sql = "SELECT r.id as id, r.content, r.is_positive, r.user_id, r.film_id, " +
                " (COUNT(rl.user_id) - COUNT(rd.user_id)) AS useful " +
                " FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "LEFT JOIN reviews_dislikes AS rd ON r.id = rd.review_id " +
                "WHERE r.id = ? " +
                "GROUP BY r.id";
        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
                try {
                    return mapRowToReview(resultSet);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, id);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sql = "SELECT r.id as id, r.content, r.is_positive, r.user_id, r.film_id, " +
                " (COUNT(rl.user_id) - COUNT(rd.user_id)) AS useful " +
                " FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "LEFT JOIN reviews_dislikes AS rd ON r.id = rd.review_id " +
                "GROUP BY r.id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            try {
                return mapRowToReview(resultSet);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, count);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Integer filmId, int count) {
        String sql = "SELECT r.id as id, r.content, r.is_positive, r.user_id, r.film_id, " +
                " (COUNT(rl.user_id) - COUNT(rd.user_id)) AS useful " +
                " FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "LEFT JOIN reviews_dislikes AS rd ON r.id = rd.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            try {
                return mapRowToReview(resultSet);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, filmId, count);
    }

    @Override
    public void putLike(int id, int userId) {
        String sql = "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ? ; " +
                " MERGE INTO reviews_likes (review_id, user_id) " +
                "VALUES (?, ?) ";
        jdbcTemplate.update(sql, id, userId, id, userId);
    }

    @Override
    public void putDislike(int id, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? ; " +
                "MERGE INTO reviews_dislikes (review_id, user_id) " +
                "VALUES (?, ?) ";
        jdbcTemplate.update(sql, id, userId, id, userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? ";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void removeDislike(int id, int userId) {
        String sql = "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ? ";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void loadReviews(Collection<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT r.id as id, r.content, r.is_positive, r.user_id, r.film_id, " +
                " (COUNT(rl.user_id) - COUNT(rd.user_id)) AS useful " +
                " FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "LEFT JOIN reviews_dislikes AS rd ON r.id = rd.review_id " +
                "WHERE r.film_id in (" + inSql + ")" +
                "GROUP BY r.id";

        jdbcTemplate.query(sqlQuery, (ResultSet rs) -> {
            //Получили из ResultSet'a идентификатор фильма и извлекли по нему из мапы значение)
            final Film film = filmById.get(rs.getInt("film_id"));
            //Добавили в коллекцию внутри объекта класса FIlm новый жанр)
            try {
                film.addReview(mapRowToReview(rs));
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
            //Преобразуем коллекцию типа Film к Integer и в массив, так как передавать требуется именно его
        }, films.stream().map(Film::getId).toArray());
    }

    private Review mapRowToReview(ResultSet resultSet) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
