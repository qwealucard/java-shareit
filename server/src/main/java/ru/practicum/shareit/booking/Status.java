package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ALL("ALL"),
    // Текущие
    CURRENT("CURRENT"),
    // Будущие
    FUTURE("FUTURE"),
    // Завершенные
    PAST("PAST"),
    APPROVED("APPROVED"),
    // Отклоненные
    REJECTED("REJECTED"),
    // Ожидающие подтверждения
    WAITING("WAITING");

    private final String value;
}