package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Capture;
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
        gameEngine.setGameState(GameEngine.GameState.RUNNING);
        MoveTree moveTree = new MoveTree(new Node<>(Node.Type.ROOT_NODE), gameEngine);
        testObj = spy(moveTree);
        gameEngine.getBoardManager().createEmptyBoard();
    }

    public Tile getTile(int index) {
        return gameEngine.getBoardManager().findTileByIndex(index);
    }

    @Test
    public void addNode_checkState() {
        assertEquals(GameEngine.GameState.RUNNING, testObj.getCurrentNode().getState().getGameState());
        Piece movingPiece = gameEngine.getBoardManager().addWhitePawn(23);
        Move<Hop> move = new Move<>(movingPiece, new Hop(getTile(23), getTile(18)));

        Node<PositionState, Move<? extends Hop>> child = testObj.addNode(testObj.getCurrentNode(), move);
        assertEquals(GameEngine.GameState.RUNNING, child.getState().getGameState());
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

        assertEquals(1, gameEngine.getBoardManager().getWhitePieces().size());
        assertFalse(move.getMovingPiece().isQueen());
    }

    @Test
    public void moveUp_restorePawns() {
        gameEngine.setIsWhiteToMove(false);
        Piece movingPiece = gameEngine.getBoardManager().addBlackPawn(28);
        Piece takenPiece1 = gameEngine.getBoardManager().addWhitePawn(33);
        Piece takenPiece2 = gameEngine.getBoardManager().addWhitePawn(43);

        Move<Capture> move = new Move<>(movingPiece);
        move.addHop(new Capture(getTile(28), getTile(39), takenPiece1));
        move.addHop(new Capture(getTile(39), getTile(48), takenPiece2));

        testObj.addNode(testObj.getCurrentNode(), move);
        testObj.moveDown(move);
        Node<PositionState, Move<? extends Hop>> previousNode = testObj.moveUp();

        assertEquals(takenPiece1, previousNode.getCondition().getMoveTakenPawns().get(0));
        assertEquals(takenPiece2, previousNode.getCondition().getMoveTakenPawns().get(1));
    }
}
