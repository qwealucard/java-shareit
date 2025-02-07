package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class UserStorage {

    @Getter
    private final Map<Long, User> users = new HashMap<>();

}
