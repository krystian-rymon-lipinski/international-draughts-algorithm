package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.algorithm.geneticalgorithm.Specimen;
import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class MainAlgorithm {

    private int depth;
    private MoveTree moveTree;
    private RewardCalculator rewardCalculator;

    public MainAlgorithm(int depth) {
        this.depth = depth;
        moveTree = new MoveTree(new Node<>(), new GameEngine());
    }

    public MainAlgorithm(int depth, GameEngine gameEngine) {
        this(depth);
        moveTree = new MoveTree(new Node<>(), gameEngine);
        rewardCalculator = new RewardCalculator(gameEngine.getBoardManager());
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public MoveTree getMoveTree() {
        return moveTree;
    }

    public void setMoveTree(MoveTree moveTree) {
        this.moveTree = moveTree;
    }

    public void calculateTree(Specimen specimen) {
        bindMovesAsNodes(depth, specimen);
    }

    private void bindMovesAsNodes(int maxLevel, Specimen specimen) {
        if (moveTree.getCurrentNode().getLevel() < maxLevel &&
                moveTree.getCurrentNode().getState().getGameState() == GameEngine.GameState.RUNNING) {

            ArrayList<Move<? extends Hop>> moves =
                    moveTree.getGameEngine().getMoveManager().findAllCorrectMoves(
                            moveTree.getGameEngine().getBoardManager(), moveTree.getGameEngine().getIsWhiteToMove());

            for (Move<? extends Hop> move : moves) {
                moveTree.addNode(moveTree.getCurrentNode(), move);
            }

            for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {
                moveTree.moveDown(node.getCondition());
                bindMovesAsNodes(maxLevel, specimen);
                moveTree.moveUp();
            }
            boolean isNodeMaximizing = moveTree.getGameEngine().getIsWhiteToMove();
            rewardCalculator.findBestChild(moveTree.getCurrentNode(), isNodeMaximizing);

        } else {
            rewardCalculator.assessPosition(moveTree.getCurrentNode(), specimen);
        }
    }

    public void updateTreeAfterMove() {
        moveTree.setCurrentNodeAsRoot();
    }

    public void calculateNextTreeLevel(int levelToCalculate, Specimen specimen) {
        if (moveTree.getCurrentNode().getLevel() == (levelToCalculate - 1)) {
            bindMovesAsNodes(levelToCalculate, specimen);
        }
        else {
            travelThroughTree(levelToCalculate, specimen);
        }
    }

    public void travelThroughTree(int levelToCalculate, Specimen specimen) {
        for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {

            moveTree.moveDown(node.getCondition());
            if (moveTree.getCurrentNode().getLevel() == (levelToCalculate - 1)) {
                bindMovesAsNodes(levelToCalculate, specimen);
            }
            else {
                travelThroughTree(levelToCalculate, specimen);
            }
            moveTree.moveUp();
        }
        boolean isNodeMaximizing = moveTree.getGameEngine().getIsWhiteToMove();
        if (!moveTree.getCurrentNode().getChildren().isEmpty()) {
            rewardCalculator.findBestChild(moveTree.getCurrentNode(), isNodeMaximizing);
        }
    }

    public Move<? extends Hop> findBestMove() {
        return rewardCalculator.findBestChild(moveTree.getCurrentNode(), moveTree.getGameEngine().getIsWhiteToMove()).
                getCondition();
    }


}
