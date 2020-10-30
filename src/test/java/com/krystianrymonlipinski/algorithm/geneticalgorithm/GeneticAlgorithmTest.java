package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

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
    public void playGame() {
        Specimen first = new Specimen(100);
        Specimen second = new Specimen(200);

        testObj.playGame(first, second);

    }
}
