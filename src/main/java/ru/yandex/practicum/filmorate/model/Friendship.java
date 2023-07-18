package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private final int id;
    private final int user1Id;
    private final int user2Id;
    private boolean isApproved;
}
