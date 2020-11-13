package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import draughts.library.managers.GameEngine;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class GeneticAlgorithmTest {

    private GeneticAlgorithm testObj;

    @Before
    public void setUp() {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        testObj = spy(geneticAlgorithm);
    }

    @Test
    public void trainAlgorithm() {
        testObj.trainAlgorithm();
    }

    @Test
    public void playTournament_playOff() {
        testObj.createFirstGeneration();
        testObj.playTournament_playOff();

        assertEquals(0, testObj.getPopulation().size());
    }

    @Test
    public void playGame() {
        Specimen first = new Specimen(-1, 10);
        Specimen second = new Specimen(-1, 10);

        GameEngine.GameState outcome = testObj.playGame(first, second);
        assertNotEquals(GameEngine.GameState.RUNNING, outcome);
    }
}
