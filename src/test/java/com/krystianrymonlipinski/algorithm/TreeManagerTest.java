package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.WhitePawn;
import draughts.library.managers.BoardManager;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TreeManagerTest {

    TreeManager testObj;

    @Mock
    GameEngine gameEngine;

    @Before
    public void setUp() {
        TreeManager treeManager = new TreeManager(new Node<>(Node.Type.ROOT_NODE));
        testObj = spy(treeManager);
        gameEngine = mock(GameEngine.class);
    }

    @Test
    public void addMoves_oneColor() {

    }

    @Test
    public void moveDown() {

    }

    @Test
    public void moveUp() {

    }
}
