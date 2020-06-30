package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MoveTreeTest {

    MoveTree testObj;

    @Mock
    GameEngine gameEngine;

    @Before
    public void setUp() {
        MoveTree moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE));
        testObj = spy(moveTree);
        gameEngine = mock(GameEngine.class);
    }

    @Test
    public void addMoves_oneColor() {
        Move<Hop> move1;
    }

    @Test
    public void moveDown() {
        Node<Integer, Move<? extends Hop>> node = mock(Node.class);
        testObj.addNode(node);
        testObj.moveDown(node.getCondition());

        verify(gameEngine.getBoardManager()).makeWholeMove(node.getCondition());
    }

    @Test
    public void moveUp() {
        Node<Integer, Move<? extends Hop>> node = mock(Node.class);
        testObj.addNode(node);
        testObj.moveDown(node.getCondition());

        verify(gameEngine.getBoardManager()).reverseWholeMove(node.getCondition());
    }
}
