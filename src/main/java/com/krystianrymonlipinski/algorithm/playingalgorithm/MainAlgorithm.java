package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.algorithm.geneticalgorithm.Specimen;
import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;
import java.util.ListIterator;

public class MainAlgorithm {

    private int depth;
    private MoveTree moveTree;
    private RewardCalculator rewardCalculator;
    private boolean usingAlphaBeta;

    public MainAlgorithm(int depth, GameEngine gameEngine) {
        this.depth = depth;
        moveTree = new MoveTree(new Node<>(), gameEngine);
        rewardCalculator = new RewardCalculator(gameEngine.getBoardManager());
        usingAlphaBeta = true;
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

    public boolean isUsingAlphaBeta() {
        return usingAlphaBeta;
    }

    public void setUsingAlphaBeta(boolean usingAlphaBeta) {
        this.usingAlphaBeta = usingAlphaBeta;
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

            ListIterator<Node<PositionState, Move<? extends Hop>>> nodeIterator =
                    moveTree.getCurrentNode().getChildren().listIterator();

            while (nodeIterator.hasNext()) {
                Node<PositionState, Move<? extends Hop>> node = nodeIterator.next();
                if (moveTree.getCurrentNode().getLevel() == 0 ||
                        node.getAncestor().getState().getAlpha() <= node.getAncestor().getState().getBeta()) {
                    moveTree.moveDown(node.getCondition());
                    if (moveTree.getCurrentNode().getLevel() < maxLevel) { //last nodes  won't need a-b parameters
                        updateAlphaBeta_movingDown();
                    }
                    bindMovesAsNodes(maxLevel, specimen);
                    Node<PositionState, Move<? extends Hop>> recentChild = moveTree.moveUp();
                    updateAlphaBeta_movingUp(recentChild);
                }
                else {
                    nodeIterator.remove();
                }
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

    public void updateAlphaBeta_movingDown() {
        moveTree.getCurrentNode().getState().setAlpha(moveTree.getCurrentNode().getAncestor().getState().getAlpha());
        moveTree.getCurrentNode().getState().setBeta(moveTree.getCurrentNode().getAncestor().getState().getBeta());
    }

    public void updateAlphaBeta_movingUp(Node<PositionState, Move<? extends Hop>> recentChild) {

        boolean isNodeMaximizing = moveTree.getGameEngine().getIsWhiteToMove();
        double recentChildValue = recentChild.getState().getRewardFunctionOutcome();

        if (isNodeMaximizing) {
            if (recentChildValue > moveTree.getCurrentNode().getState().getAlpha()) {
                moveTree.getCurrentNode().getState().setAlpha(recentChildValue);
            }
        }
        else {
            if (recentChildValue < moveTree.getCurrentNode().getState().getBeta()) {
                moveTree.getCurrentNode().getState().setBeta(recentChildValue);
            }
        }
    }


}
