package com.snake.game.exception;

import java.io.Serial;

public class GameOverException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public GameOverException(String msg) {
        super(msg);
    }
}
