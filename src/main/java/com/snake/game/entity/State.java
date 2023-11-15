package com.snake.game.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {

    private String gameId;

    private Integer width;

    private Integer height;

    private Integer score;

    private Fruit fruit;

    private Snake snake;


}
