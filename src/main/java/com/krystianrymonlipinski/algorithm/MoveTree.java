package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.exceptions.NoAncestorForRootNodeException;
import com.krystianrymonlipinski.exceptions.NodeWithNoChildrenException;
import com.krystianrymonlipinski.tree.model.Node;
import com.krystianrymonlipinski.tree.model.Tree;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;


public class MoveTree extends Tree<Integer, Move<? extends Hop>> {

    private GameEngine gameEngine;

    public MoveTree(Node<Integer, Move<? extends Hop>> root) {
        super(root);
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

    public void moveUp() {
        try {
            super.moveUp();
            gameEngine.getBoardManager().reverseWholeMove(move);
            gameEngine.finishMove(move);
        } catch (NoAncestorForRootNodeException ex) {
            ex.printStackTrace();
        }
    }

}
