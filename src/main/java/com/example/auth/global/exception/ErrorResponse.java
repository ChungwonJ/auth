package com.example.auth.global.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String status;
    private int code;
    private String message;
    private Instant timestamp;

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String message) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(status.name())
                        .code(status.value())
                        .message(message)
                        .timestamp(Instant.now())
                        .build(),
                status
        );
    }

}
