package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import com.krystianrymonlipinski.algorithm.playingalgorithm.MainAlgorithm;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import java.util.ArrayList;

public class GeneticAlgorithm {

    private final static int NUMBER_OF_GENERATIONS = 20;
    private final static int NUMBER_OF_SPECIMENS = 16;
    private ArrayList<Specimen> specimens = new ArrayList<>();

    private final static int ALGORITHM_DEPTH = 4;


    public ArrayList<Specimen> getSpecimens() {
        return specimens;
    }

    public void createFirstGeneration() {
        for (int i=0; i<NUMBER_OF_SPECIMENS; i++) {
            specimens.add(new Specimen(1000));
            System.out.println(specimens.get(i));
        }
    }


    public void playGame(Specimen first, Specimen second) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
        boolean firstToMove = true;

        MainAlgorithm mainAlgorithm = new MainAlgorithm(ALGORITHM_DEPTH, gameEngine);
        mainAlgorithm.calculateTree(first);

        while(gameEngine.getGameState() == GameEngine.GameState.RUNNING) {
            Move<? extends Hop> move = mainAlgorithm.findBestMove();

            mainAlgorithm.getMoveTree().moveDown(move);
            mainAlgorithm.getMoveTree().setCurrentNodeAsRoot();

            firstToMove = !firstToMove;
            int levelToCalculate = ALGORITHM_DEPTH + mainAlgorithm.getMoveTree().getRoot().getLevel();
            if (firstToMove) {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate, first);
            } else {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate, second);
            }
        }
    }
}
