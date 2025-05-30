package ru.practicum.shareit.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("Объект не найден");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<ErrorResponse> handleValidateException(ValidateException e) {
        log.error("Ошибка с заполнением полей");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        log.error("Ошибка с редактированием чужой вещи");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BookingNotAvailableItemsException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotAvailableItemsException(BookingNotAvailableItemsException e) {
        log.error("Вещь не доступна для бронирования");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommentNotBookingItemException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotBookingItemException(CommentNotBookingItemException e) {
        log.error("Вы не бронировали эту вещь");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStatusException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStatusException(IllegalStatusException e) {
        log.error("Недопустимый статус");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}
