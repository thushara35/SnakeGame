package com.snake.game.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
public class GameRequest {

    private State state;
    private List<Tick> ticks;
}
