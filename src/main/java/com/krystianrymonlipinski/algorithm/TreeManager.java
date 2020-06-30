package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.exceptions.NodeWithNoChildrenException;
import com.krystianrymonlipinski.tree.model.Node;
import com.krystianrymonlipinski.tree.model.Tree;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public class TreeManager extends Tree<Integer, Move<? extends Hop>> {

    private GameEngine gameEngine;

    public TreeManager(Node<Integer, Move<? extends Hop>> root) {
        super(root);
    }

    public void addMoves(boolean colorToMove) {
        gameEngine.getMoveManager().findAllCorrectMoves(gameEngine.getBoardManager(), colorToMove);
    }

    public void moveDown(Move<? extends Hop> move) {
        try {
            super.moveDown(move);
            gameEngine.updateBoard(move);
            gameEngine.finishMove(move);
        } catch(NodeWithNoChildrenException ex) {
            ex.printStackTrace();
        }
    }

    public void moveUp(Move<? extends Hop> move) {

    }

}
