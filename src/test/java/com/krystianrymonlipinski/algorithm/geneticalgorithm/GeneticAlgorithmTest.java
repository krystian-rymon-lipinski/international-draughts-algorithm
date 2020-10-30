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
    public void createFirstGeneration() {
        testObj.createFirstGeneration();
        assertEquals(16, testObj.getSpecimens().size());
    }

    @Test
    public void playTournament() {
        testObj.createFirstGeneration();
        testObj.playTournament();

        int numberOfPlayers = testObj.getSpecimens().size();
        int numberOfMatchesToPlay = numberOfPlayers * (numberOfPlayers-1)/2;

        verify(testObj, times(numberOfMatchesToPlay)).playGame(any(Specimen.class), any(Specimen.class));
    }

    @Test
    public void playGame() {
        Specimen first = new Specimen(100);
        Specimen second = new Specimen(200);

        GameEngine.GameState outcome = testObj.playGame(first, second);
        assertNotEquals(GameEngine.GameState.RUNNING, outcome);
    }
}
