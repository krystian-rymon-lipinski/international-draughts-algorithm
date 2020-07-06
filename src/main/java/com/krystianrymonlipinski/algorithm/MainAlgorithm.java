package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class MainAlgorithm {

    private int depth;
    private MoveTree moveTree;
    private boolean isPlayedColorWhite;

    public MainAlgorithm(int depth) {
        this.depth = depth;
        moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), new GameEngine());
    }

    public MainAlgorithm(int depth, GameEngine gameEngine) {
        this(depth);
        moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), gameEngine);
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

    public void calculateTree() {
        bindMovesAsNodes(depth);
    }

    private void bindMovesAsNodes(int maxLevel) {
        System.out.println("Check if nodes should be bound");
        if (moveTree.getCurrentNode().getLevel() < maxLevel) {
            System.out.println("Binding nodes...");
            ArrayList<Move<? extends Hop>> moves = moveTree.getGameEngine().prepareMove(moveTree.getGameEngine().getIsWhiteToMove());
            moveTree.getCurrentNode().getState().setGameState(getMoveTree().getGameEngine().getGameState());

            if (moveTree.getCurrentNode().getState().getGameState() == GameEngine.GameState.RUNNING) {

                for (Move<? extends Hop> move : moves) {
                    moveTree.addNode(moveTree.getCurrentNode(), move);
                }

                for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {
                    moveTree.moveDown(node.getCondition());
                    //rewardCalculator.assessPosition();
                    bindMovesAsNodes(maxLevel);
                    moveTree.moveUp();
                }
            }
        }
    }

    public void updateTreeAfterMove(Move<? extends Hop> move) {
        moveTree.moveDown(move);
        moveTree.setChildAsNewRoot(moveTree.getCurrentNode());
    }

    public void calculateTreeLevel(int level) throws ChosenLevelAlreadyCalculatedException {
        if (level <= depth) {
            if (moveTree.getCurrentNode().getChildren().size() > 0) {
                travelThroughTree(level);
            }
            else {
                //isLevelAlreadyCalculated(level);
                bindMovesAsNodes(level);
            }
        }
    }

    public void travelThroughTree(int level) throws ChosenLevelAlreadyCalculatedException {
        System.out.println("Travel through tree");
        isLevelAlreadyCalculated(level);
        for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {
            moveTree.moveDown(node.getCondition());
            if (moveTree.getCurrentNode().getChildren().size() > 0) {
                travelThroughTree(level);
            }
            else {
                System.out.println("Moves should be found");
                bindMovesAsNodes(level);
                moveTree.moveUp();
            }
        }
    }

    public void isLevelAlreadyCalculated(int level) throws ChosenLevelAlreadyCalculatedException {
        if (moveTree.getCurrentNode().getLevel() >= level) {
            throw new ChosenLevelAlreadyCalculatedException("Level " + level + " is already calculated!");
        }
    }


}
