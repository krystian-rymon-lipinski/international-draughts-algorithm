package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.exceptions.NoAncestorForRootNodeException;
import com.krystianrymonlipinski.exceptions.NodeConditionNotFoundException;
import com.krystianrymonlipinski.exceptions.NodeWithNoChildrenException;
import com.krystianrymonlipinski.tree.model.Node;
import com.krystianrymonlipinski.tree.model.Tree;
import draughts.library.boardmodel.Piece;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;


public class MoveTree extends Tree<PositionState, Move<? extends Hop>> {

    private GameEngine gameEngine;

    public MoveTree(Node<PositionState, Move<? extends Hop>> root) {
        super(root);
    }

    public MoveTree(Node<PositionState, Move<? extends Hop>> root, GameEngine gameEngine) {
        this(root);
        this.gameEngine = gameEngine;
        root.setState(new PositionState(gameEngine.getGameState()));
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public Node<PositionState, Move<? extends Hop>> addNode(Node<PositionState, Move<? extends Hop>> node, Move<? extends Hop> move) {
        Node<PositionState, Move<? extends Hop>> newNode = super.addNode(node, move);
        newNode.setState(new PositionState(gameEngine.getGameState()));
        return newNode;
    }

    public void moveDown(Move<? extends Hop> move) {
        try {
            super.moveDown(move);
            gameEngine.getBoardManager().makeWholeMove(move);
            gameEngine.finishMove(move);
        } catch(NodeWithNoChildrenException | NodeConditionNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Node<PositionState, Move<? extends Hop>> moveUp() {
        try {
            Node<PositionState, Move<? extends Hop>> previousNode = super.moveUp();
            ArrayList<Piece> piecesToReturn = gameEngine.getBoardManager().reverseWholeMove(previousNode.getCondition());
            if (previousNode.getCondition().getIsPromotion()) {
                Piece demotedPawn = gameEngine.getBoardManager().demoteQueen(previousNode.getCondition().getMovingPiece());
                previousNode.getCondition().setMovingPiece(demotedPawn);
            }
            if (previousNode.getCondition().isCapture()) {
                previousNode.getCondition().setMoveTakenPawns(piecesToReturn);
            }
            gameEngine.changeColor();
            return previousNode;
        } catch (NoAncestorForRootNodeException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
