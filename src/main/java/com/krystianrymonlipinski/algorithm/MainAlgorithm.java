package com.krystianrymonlipinski.algorithm;

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
        moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), new GameEngine());
    }

    public MainAlgorithm(int depth, GameEngine gameEngine) {
        this(depth);
        moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), gameEngine);
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

    public void calculateTree() {
        bindMovesAsNodes(depth);
        System.out.println("Calculated nodes for the whole tree: " + moveTree.getNodes().size());
    }

    private void bindMovesAsNodes(int maxLevel) {
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
                bindMovesAsNodes(maxLevel);
                moveTree.moveUp();
            }
            boolean isNodeMaximizing = moveTree.getGameEngine().getIsWhiteToMove();
            rewardCalculator.findBestChild(moveTree.getCurrentNode(), isNodeMaximizing);

        } else {
            rewardCalculator.assessPosition(moveTree.getCurrentNode());
        }
    }

    public void updateTreeAfterMove(Move<? extends Hop> move) {
        moveTree.moveDownAndSetChildAsNewRoot(move);
    }

    public void calculateNextTreeLevel(int levelToCalculate) {
        System.out.println("Nodes before calculated level: " + moveTree.getNodes().size());
        if (levelToCalculate <= depth) {
            if (moveTree.getCurrentNode().getLevel() == (levelToCalculate - 1)) {
                bindMovesAsNodes(levelToCalculate);
            }
            else {
                travelThroughTree(levelToCalculate);
            }
        }

        System.out.println("Nodes after calculated level: " + moveTree.getNodes().size());
    }

    public void travelThroughTree(int levelToCalculate) {
        for (Node<PositionState, Move<? extends Hop>> node : moveTree.getCurrentNode().getChildren()) {

            moveTree.moveDown(node.getCondition());
            if (moveTree.getCurrentNode().getLevel() == (levelToCalculate - 1)) {
                bindMovesAsNodes(levelToCalculate);
            }
            else {
                travelThroughTree(levelToCalculate);
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
