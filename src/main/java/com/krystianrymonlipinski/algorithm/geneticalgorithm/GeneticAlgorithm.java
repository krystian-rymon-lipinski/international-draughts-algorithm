package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import com.krystianrymonlipinski.algorithm.playingalgorithm.MainAlgorithm;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class GeneticAlgorithm {

    private final static int NUMBER_OF_GENERATIONS = 20;
    private final static int NUMBER_OF_SPECIMENS = 16;
    private ArrayList<Specimen> specimens;

    private final static int ALGORITHM_DEPTH = 6;







    public void playGame(Specimen first, Specimen second) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
        boolean firstToMove = true;

        MainAlgorithm mainAlgorithm = new MainAlgorithm(ALGORITHM_DEPTH, gameEngine);
        mainAlgorithm.calculateTree(); //TODO: change assessing position based on list of arguments: if it's empty, assume all parameters are 1, of not change the function

        //TODO: or maybe just change the signature or perform null check - if there is no specimen passed as an argument, then assess positions with 1 as weights

        while(gameEngine.getGameState() == GameEngine.GameState.RUNNING) {
            Move<? extends Hop> move = mainAlgorithm.findBestMove();

            mainAlgorithm.getMoveTree().moveDown(move);
            mainAlgorithm.getMoveTree().setCurrentNodeAsRoot();

            firstToMove = !firstToMove;
            int levelToCalculate = ALGORITHM_DEPTH + mainAlgorithm.getMoveTree().getRoot().getLevel();
            if (firstToMove) {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate); //TODO: change assessing position based on list of arguments: if it's empty, assume all parameters are 1, of not change the function
            } else {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate);
            }

        }
    }
}
