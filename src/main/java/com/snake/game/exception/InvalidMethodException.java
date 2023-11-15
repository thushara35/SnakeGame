package com.snake.game.exception;

import java.io.Serial;

public class InvalidMethodException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidMethodException(String msg) {
        super(msg);
    }
}
