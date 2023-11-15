package com.snake.game.service;

import com.snake.game.entity.*;
import com.snake.game.utill.SnakeGameConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service
public class SnakeGameServiceImpl implements SnakeGameService {

    private final List<String> gemeIdList = new ArrayList<>();

    Logger logger = LoggerFactory.getLogger(SnakeGameServiceImpl.class);

    @Override
    public State createStateObject(Integer width, Integer height) {
        logger.info("getNewGame start width :{} height:{} ", width, height);
        if (width >= 2 && height >= 2) {
            Snake snake = new Snake(0, 0, 1, 0);

            String gameId = gameIdGenerator();
            this.gemeIdList.add(gameId);
            return new State(gameId, width, height, 0, fruitCoordinationCreator(width, height, snake), snake);
        } else {
            logger.error("width and height must be greater than or equal 2");
        }
        return null;
    }

    @Override
    public Object validateRequestAndCountScore(GameRequest gameRequest) {
        logger.info("validateRequestAndCountScore start gameRequest :{}", gameRequest);

        if (Objects.nonNull(gameRequest.getState())) {
            if (Objects.nonNull(gameRequest.getTicks()) && !gameRequest.getTicks().isEmpty()) {
                State state = gameRequest.getState();
                List<Tick> ticks = gameRequest.getTicks();
                if (this.gemeIdList.contains(state.getGameId())) {
                    boolean isValid = false;
                    for (Tick tick : ticks) {
                        if (checkTicksAreValid(tick, state)) {

                            isValid = Objects.equals(state.getSnake().getX(), state.getFruit().getX()) && Objects.equals(state.getSnake().getY(), state.getFruit().getY());

                        } else {
                            logger.info("Invalid Ticks Id");
                            return new FailedResponse(SnakeGameConstant.INVALID_TICKS, "Given ticks didn't reach to fruit ");
                        }
                    }
                    if (isValid) {
                        state.setScore(state.getScore() + 1);
                        state.setFruit(fruitCoordinationCreator(state.getWidth(), state.getHeight(), state.getSnake()));
                        return state;
                    } else {
                        return new FailedResponse(SnakeGameConstant.INVALID_TICKS, "Given ticks are wrong Games Over");
                    }


                } else {
                    logger.info("Invalid Game Id");
                    return new FailedResponse(SnakeGameConstant.INVALID_GAME_ID, "Given GameId not in system :" + state.getGameId());
                }
            } else {
                logger.info("Ticks is empty");

                return new FailedResponse(SnakeGameConstant.TICKS_EMPTY, "Given Ticks Empty");
            }
        } else {
            logger.info("State is empty");

            return new FailedResponse(SnakeGameConstant.STATE_EMPTY, "Given State Empty");
        }
    }

    private boolean checkTicksAreValid(Tick tick, State state) {
        logger.info("checkTicksAreValid start ticks :{} state:{}", tick, state);
        Snake snake = state.getSnake();

        if (Objects.equals(Math.abs(tick.getVelX()), Math.abs(tick.getVelY()))) {
            logger.info("velX and velY are equal");
            return false;
        } else {
            if (!(1 == Math.abs(tick.getVelX()) || Math.abs(tick.getVelX()) == 0)
                    && !(1 == Math.abs(tick.getVelY()) || Math.abs(tick.getVelY()) == 0)) {
                logger.info("1>=(+)velX,velY>=0 ");
                return false;
            } else {
                if (checkValidDirectionOrNot(tick.getVelX(), snake.getVelX())
                        && checkValidDirectionOrNot(tick.getVelY(), snake.getVelY())) {
                    logger.info("direction valid");
                    int newXCoordinate = snake.getX() + snake.getVelX();
                    int newYCoordinate = snake.getY() + (-1 * snake.getVelY());
                    logger.info("new direction correct newXCoordinate :{} newYCoordinate:{}", newXCoordinate, newYCoordinate);
                    if ((state.getWidth() - 1) >= newXCoordinate && newXCoordinate >= 0
                            && (state.getHeight() - 1) >= newYCoordinate && newYCoordinate >= 0) {
                        snake.setVelX(tick.getVelX());
                        snake.setVelY(tick.getVelY());
                        snake.setX(newXCoordinate);
                        snake.setY(newYCoordinate);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public boolean checkValidDirectionOrNot(Integer velXOrY, Integer snakeVelXorY) {
        logger.info("checkValidDirectionOrNot start velXOrY :{} snakeVelXorY:{}", velXOrY, snakeVelXorY);
        if (velXOrY == 0 || snakeVelXorY == 0) {
            return true;
        }
        logger.info("checkValidDirectionOrNot start return :{}", (snakeVelXorY * -1 != velXOrY));
        return snakeVelXorY * -1 != velXOrY;

    }

    public Fruit fruitCoordinationCreator(Integer width, Integer height, Snake snake) {
        logger.info("fruitCoordinationCreator start width :{} height:{} Snake :{}", width, height, snake);

        int fruitX = 0;
        int fruitY = 0;

        do {
            fruitX = getRandomNumber(0, width);
            fruitY = getRandomNumber(0, height);

        } while ((fruitX == snake.getX() && fruitY == snake.getY()));


        logger.info("fruitX :{} fruitY:{}", fruitX, fruitY);
        return new Fruit(fruitX, fruitY);

    }

    public String gameIdGenerator() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        String gameId = java.text.MessageFormat.format("SNAKE{0}", dateFormat.format(new Date()));

        logger.info("gameId :{}}", gameId);
        return gameId;
    }


    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
