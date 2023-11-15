package com.snake.game.controller;

import com.snake.game.entity.FailedResponse;
import com.snake.game.entity.GameRequest;
import com.snake.game.entity.State;
import com.snake.game.exception.ControllerExceptionHandler;
import com.snake.game.exception.ResourceNotFoundException;
import com.snake.game.service.SnakeGameService;
import com.snake.game.utill.SnakeGameConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/game-board")
public class SnakeGameController {


    public static final Logger logger = LoggerFactory.getLogger(SnakeGameController.class);


    @Autowired
    private SnakeGameService snakeGameService;

    @Autowired
    private ControllerExceptionHandler controllerExceptionHandler;

    @GetMapping("/new")
    public ResponseEntity<State> getNewGame(@RequestParam Integer width, @RequestParam Integer height) {
        logger.info("getNewGame start width :{} height:{} ", width, height);
        ResponseEntity<State> response = null;
        try {
            if (Objects.nonNull(width) && Objects.nonNull(height)) {
                State state = snakeGameService.createStateObject(width, height);
                if (Objects.nonNull(state)) {
                    response = ResponseEntity.status(HttpStatus.OK).body(state);
                } else {
                    response = ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }
            } else {
                throw new ResourceNotFoundException(" Invalid request.");
            }
        } catch (Exception e) {
            logger.error(e.toString());
            throw new ResourceNotFoundException(" Invalid request. check width and height in valid format");
        }
        logger.info("getNewGame start END response :{}", response);
        return response;
    }


    @PostMapping("/validate")
    public ResponseEntity<Object> scoreValidator(@RequestBody GameRequest gameRequest) {
        logger.info("scoreValidator start");
        ResponseEntity<Object> response = null;
        try {
            if (Objects.nonNull(gameRequest)) {
                logger.info("gameRequest : {}", gameRequest);
                Object stateObject = snakeGameService.validateRequestAndCountScore(gameRequest);
                logger.info("stateObject :{}", stateObject);
                if (Objects.nonNull(stateObject)) {
                    if (stateObject instanceof State) {
                        response = ResponseEntity.status(HttpStatus.OK).body(stateObject);
                    } else if (stateObject instanceof FailedResponse failedResponse) {

                        if (failedResponse.getFailedCode().equalsIgnoreCase(SnakeGameConstant.INVALID_GAME_ID) ||
                                failedResponse.getFailedCode().equalsIgnoreCase(SnakeGameConstant.STATE_EMPTY) ||
                                failedResponse.getFailedCode().equalsIgnoreCase(SnakeGameConstant.TICKS_EMPTY)) {
                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(failedResponse.getFailedDesc());
                        } else if (failedResponse.getFailedCode().equalsIgnoreCase(SnakeGameConstant.INVALID_TICKS)) {
                            response = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                                    .body(failedResponse.getFailedDesc());
                        }
                    }
                } else {
                    logger.info("stateObject is null");
                    response = ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }
            } else {
                logger.info("gameRequest is null");
                response = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        logger.info("scoreValidator END");
        return response;
    }
}
