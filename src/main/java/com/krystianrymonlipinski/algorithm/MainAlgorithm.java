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
        bindMovesAsNodes();
    }

    public void bindMovesAsNodes() {
        if (moveTree.getCurrentNode().getLevel() < depth) {

            ArrayList<Move<? extends Hop>> moves = moveTree.getGameEngine().prepareMove(moveTree.getGameEngine().getIsWhiteToMove());
            moveTree.getCurrentNode().getState().setGameState(getMoveTree().getGameEngine().getGameState());

            if (moveTree.getCurrentNode().getState().getGameState() == GameEngine.GameState.RUNNING) {

                for (Move<? extends Hop> move : moves) {
                    moveTree.addNode(moveTree.getCurrentNode(), move);
                }

                for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {
                    moveTree.moveDown(node.getCondition());
                    //rewardCalculator.assessPosition();
                    bindMovesAsNodes();
                    moveTree.moveUp();
                }
            }
        }
    }

    public void updateTreeAfterMove(Move<? extends Hop> move) {
        moveTree.moveDown(move);
        moveTree.setChildAsNewRoot(moveTree.getCurrentNode());
    }

    public void calculateTreeLevel(int level) {

    }
}
