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
        ArrayList<Move<? extends Hop>> moves = moveTree.getGameEngine().prepareMove(moveTree.getGameEngine().getIsWhiteToMove());
        for(Move<? extends Hop> move : moves) {
            System.out.println(move);
        }

        for(Move<? extends Hop> move : moves) {
            moveTree.addNode(moveTree.getCurrentNode(), move);
        }

        for(Node<Integer, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {
            System.out.println("Move: " + node.getCondition());
            moveTree.moveDown(node.getCondition());
            //rewardCalculator.assessPosition();
            if(moveTree.getCurrentNode().getLevel() < depth) bindMovesAsNodes();
            moveTree.moveUp();
        }

    }
}
