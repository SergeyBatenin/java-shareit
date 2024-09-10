package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(Exception exception) {
        log.error("ERROR", exception);
        final ByteArrayOutputStream out = getOutputStream(exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage(), out.toString(StandardCharsets.UTF_8)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handle(Exception exception) {
        log.error("ERROR", exception);
        final ByteArrayOutputStream out = getOutputStream(exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(exception.getMessage(), out.toString(StandardCharsets.UTF_8)));
    }

    private static ByteArrayOutputStream getOutputStream(Exception exception) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(out, true, StandardCharsets.UTF_8));
        return out;
    }
}
