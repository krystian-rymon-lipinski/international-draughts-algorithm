package com.krystianrymonlipinski.algorithm;

import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class MainAlgorithm {

    private int depth;
    private MoveTree moveTree;
    GameEngine gameEngine;


    public MainAlgorithm(int depth) {
        this.depth = depth;
        gameEngine = new GameEngine();
    }

    public void calculateTree() {
        bindMovesAsNodes();

    }

    public void bindMovesAsNodes() {
        ArrayList<Move<? extends Hop>> moves = gameEngine.getMoveManager().findAllCorrectMoves(gameEngine.getBoardManager(), true);

        for(Move<? extends Hop> move : moves) {
            moveTree.addNode(moveTree.getCurrentNode(), move);
            moveTree.moveDown(move);
            //rewardCalculator.assessPosition();
            if(moveTree.getCurrentNode().getLevel() < depth) bindMovesAsNodes();
            moveTree.moveUp();
        }

    }
}
