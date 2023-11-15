package com.snake.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snake.game.entity.*;
import com.snake.game.service.SnakeGameService;
import com.snake.game.utill.SnakeGameConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SnakeGameController.class)
class SnakeGameControllerTest {
    @MockBean
    private SnakeGameService snakeGameService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getNewGame() throws Exception {

        Snake snake = new Snake(0, 0, 1, 0);
        State state = new State("Test123", 2, 2, 0, new Fruit(1, 1), snake);

        when(snakeGameService.createStateObject(2, 2)).thenReturn(state);
        mockMvc.perform(get("/game-board/new").param("width","2").param("height","2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").isNotEmpty())
                .andExpect(jsonPath("$.score").value(state.getScore()))
                .andExpect(jsonPath("$.width").value(state.getWidth()))
                .andExpect(jsonPath("$.height").value(state.getHeight()))
                .andExpect(jsonPath("$.fruit").isNotEmpty())
                .andExpect(jsonPath("$.snake").value(snake))
                .andDo(print());

        when(snakeGameService.createStateObject(1, 1)).thenReturn(null);
        mockMvc.perform(get("/game-board/new").param("width","1").param("height","1")).andExpect(status().isNoContent())
                .andDo(print());

        when(snakeGameService.createStateObject(0, 0)).thenReturn(null);
        mockMvc.perform(get("/game-board/new").param("width","0").param("height","0")).andExpect(status().isNoContent())
                .andDo(print());

    }

    @Test
    void scoreValidator() throws Exception {

        State state = new State("Test123", 2, 2, 0, new Fruit(1, 1), new Snake(0, 0, 1, 0));
        List<Tick> ticks = new ArrayList<>();
        ticks.add(new Tick(0, -1));
        ticks.add(new Tick(-1, 0));
        GameRequest request = new GameRequest(state, ticks);

        State responseState = new State("Test123", 2, 2, 1, new Fruit(1, 0), new Snake(0, 0, -1, 0));


        when(snakeGameService.validateRequestAndCountScore(request)).thenReturn(responseState);
        mockMvc.perform(post("/game-board/validate").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        // .content(.value("Test1234"))
                ).andExpect(status().isOk())
                .andDo(print());

        state = new State("Test123", 2, 2, 0, new Fruit(1, 1), new Snake(0, 0, 1, 0));
        ticks = new ArrayList<>();
        ticks.add(new Tick(0, -1));
        ticks.add(new Tick(2, 0));
        request = new GameRequest(state, ticks);

        FailedResponse failedResponse = new FailedResponse(SnakeGameConstant.INVALID_TICKS, "Invalid ticks");
        when(snakeGameService.validateRequestAndCountScore(request)).thenReturn(failedResponse);
        mockMvc.perform(post("/game-board/validate").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        // .content(.value("Test1234"))
                ).andExpect(status().isIAmATeapot())
                .andDo(print());
    }


}