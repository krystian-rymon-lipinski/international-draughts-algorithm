package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MoveTreeTest {

    MoveTree testObj;
    GameEngine gameEngine;

    @Before
    public void setUp() {
        gameEngine = new GameEngine();
        MoveTree moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), gameEngine);
        testObj = spy(moveTree);
        gameEngine.getBoardManager().createEmptyBoard();
    }

    public Tile getTile(int index) {
        return gameEngine.getBoardManager().findTileByIndex(index);
    }

    @Test
    public void moveDown() {
        gameEngine.setIsWhiteToMove(true);
        Piece movingPiece = gameEngine.getBoardManager().addWhitePawn(23);
        Move<Hop> move = new Move<>(movingPiece, new Hop(getTile(23), getTile(18)));

        testObj.addNode(testObj.getCurrentNode(), move);
        testObj.moveDown(move);

        assertEquals(getTile(18), movingPiece.getPosition());
        assertFalse(gameEngine.getIsWhiteToMove());
    }

    @Test
    public void moveDown_pawnPromotion() {
        gameEngine.setIsWhiteToMove(true);
        Piece movingPiece = gameEngine.getBoardManager().addWhitePawn(9);
        Move<Hop> move = new Move<>(movingPiece, new Hop(getTile(9), getTile(4)));

        testObj.addNode(testObj.getCurrentNode(), move);
        testObj.moveDown(move);

        assertTrue(move.getMovingPiece().isQueen());
    }

    @Test
    public void moveUp() {
        gameEngine.setIsWhiteToMove(true);
        Piece movingPiece = gameEngine.getBoardManager().addWhitePawn(23);
        Move<Hop> move = new Move<>(movingPiece, new Hop(getTile(23), getTile(18)));
        testObj.addNode(testObj.getCurrentNode(), move);
        testObj.moveDown(move);

        assertFalse(gameEngine.getIsWhiteToMove());

        testObj.moveUp();

        assertEquals(getTile(23), move.getMovingPiece().getPosition());
        assertTrue(gameEngine.getIsWhiteToMove());
    }

    @Test
    public void moveUp_queenDemotion() {
        gameEngine.setIsWhiteToMove(true);
        Piece movingPiece = gameEngine.getBoardManager().addWhitePawn(9);
        Move<Hop> move = new Move<>(movingPiece, new Hop(getTile(9), getTile(4)));

        testObj.addNode(testObj.getCurrentNode(), move);
        testObj.moveDown(move);

        assertTrue(move.getMovingPiece().isQueen());

        testObj.moveUp();

        assertFalse(move.getMovingPiece().isQueen());
    }
}
