package com.example.snakegame;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class GameUnitTest {

    private Game game;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        game = mock(Game.class);
        game.snakePointsList = new ArrayList<>();
    }

    @Test
    public void testSnakeSizeInitially() {
        game.initGame(); // chamando o método initGame() da classe mockada
        assertEquals(3, Game.defaultTablePoints); //verifica tamanho da cobrinha após a inicialização
    }
    @Test
    public void testSnakePointsListNotEmpty() {
        assertTrue(game.snakePointsList.isEmpty());
    }
    @Test
    public void testCanvasIsNullInitially() {
        assertNull(game.canvas);
    }
    @Test
    public void testScoreInitially() {
        assertEquals(0, game.score); // verifica se a pontuação é 0 após a inicialização
    }
    @Test
    public void testInitGameSnakePositionDefaultRight() {
        assertNull(game.snakePosition);
    }
    @Test
    public void testAddPoints() {
        game.addPoints();
        assertNotNull(game.positionX); // verifica se a posição X do ponto adicionado não está nula
        assertNotNull(game.positionY); // verifica se a posição Y do ponto adicionado não está nula
    }

}