package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.exceptions.NoAncestorForRootNodeException;
import com.krystianrymonlipinski.exceptions.NodeWithNoChildrenException;
import com.krystianrymonlipinski.tree.model.Node;
import com.krystianrymonlipinski.tree.model.Tree;
import draughts.library.boardmodel.Piece;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;


public class MoveTree extends Tree<Integer, Move<? extends Hop>> {

    private GameEngine gameEngine;

    public MoveTree(Node<Integer, Move<? extends Hop>> root) {
        super(root);
    }

    public MoveTree(Node<Integer, Move<? extends Hop>> root, GameEngine gameEngine) {
        this(root);
        this.gameEngine = gameEngine;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void moveDown(Move<? extends Hop> move) {
        try {
            super.moveDown(move);
            gameEngine.getBoardManager().makeWholeMove(move);
            gameEngine.finishMove(move);
        } catch(NodeWithNoChildrenException ex) {
            ex.printStackTrace();
        }
    }

    public Node<Integer, Move<? extends Hop>> moveUp() {
        try {
            Node<Integer, Move<? extends Hop>> previousNode = super.moveUp();
            ArrayList<Piece> piecesToReturn = gameEngine.getBoardManager().reverseWholeMove(previousNode.getCondition());
            if (previousNode.getCondition().getIsPromotion()) {
                Piece demotedPawn = gameEngine.getBoardManager().demoteQueen(previousNode.getCondition().getMovingPiece());
                previousNode.getCondition().setMovingPiece(demotedPawn);
            }
            if (previousNode.getCondition().isCapture()) {
                for (Piece piece : previousNode.getCondition().getMoveTakenPawns()) {
                    previousNode.getCondition().getMoveTakenPawns()
                }
            }
            gameEngine.changeColor();
            return previousNode;
        } catch (NoAncestorForRootNodeException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
