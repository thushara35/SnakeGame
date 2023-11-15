package com.snake.game.service;

import com.snake.game.entity.GameRequest;
import com.snake.game.entity.State;
import org.springframework.stereotype.Service;

@Service
public interface SnakeGameService {
    State createStateObject(Integer width, Integer height);

    Object validateRequestAndCountScore(GameRequest gameRequest);
}
