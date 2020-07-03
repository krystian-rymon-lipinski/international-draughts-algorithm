package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MainAlgorithmTest {

    MainAlgorithm testObj;
    MoveTree moveTree;
    BoardManager boardManager;

    @Before
    public void setUp() {
        testObj = new MainAlgorithm(10);
        moveTree = testObj.getMoveTree();
        boardManager = moveTree.getGameEngine().getBoardManager();
        boardManager.createEmptyBoard();

    }

    public Tile getTile(int index) {
        return boardManager.findTileByIndex(index);
    }

    @Test
    public void findNodes_oneLevelDeep() {
        boardManager.addWhitePawn(28);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(1);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(3, testObj.getMoveTree().getNodes().size());
        for(Node<Integer, Move<? extends Hop>> node : testObj.getMoveTree().getCurrentNode().getChildren()) {
            assertEquals(getTile(28), node.getCondition().getMovingPiece().getPosition());
        }
    }

    @Test
    public void findNodes_twoLevelsDeep() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(8);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(7, testObj.getMoveTree().getNodes().size());
        for(Node<Integer, Move<? extends Hop>> node : testObj.getMoveTree().getNodes()) {
            if (node.getLevel() == 2) {
                assertEquals(getTile(8), node.getCondition().getMovingPiece().getPosition());
            }
        }
    }

    @Test
    public void findNodes_sixLevelsDeep() {
        boardManager.addWhitePawn(46);
        boardManager.addBlackPawn(3);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(52, testObj.getMoveTree().getNodes().size());

        int[] numberOfNodesOnLevel = new int[7];
        for(Node<Integer, Move<? extends Hop>> node : testObj.getMoveTree().getNodes()) {
            numberOfNodesOnLevel[node.getLevel()]++;
        }
        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(1, numberOfNodesOnLevel[1]);
        assertEquals(2, numberOfNodesOnLevel[2]);
        assertEquals(4, numberOfNodesOnLevel[3]);
        assertEquals(8, numberOfNodesOnLevel[4]);
        assertEquals(12, numberOfNodesOnLevel[5]);
        assertEquals(24, numberOfNodesOnLevel[6]);
    }

    @Test
    public void findNodes_withCaptureInTheTree() {

    }

    @Test
    public void findNodes_withPossibilityOfWinningInTheTree() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(19);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(52, testObj.getMoveTree().getNodes().size());
    }

}
