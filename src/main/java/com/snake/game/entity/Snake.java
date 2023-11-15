package com.snake.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snake {
    private Integer x;
    private Integer y;

    private Integer velX;
    private Integer velY;
}
