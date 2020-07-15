package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.exceptions.NoAncestorForRootNodeException;
import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.BoardManager;
import draughts.library.managers.DrawArbiter;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Capture;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MainAlgorithmTest {

    MainAlgorithm testObj;
    MoveTree moveTree;
    BoardManager boardManager;

    @Before
    public void setUp() {
        GameEngine gameEngine = new GameEngine();
        gameEngine.setGameState(GameEngine.GameState.RUNNING);
        testObj = new MainAlgorithm(10, gameEngine);
        testObj.setPlayedColorWhite(true);
        moveTree = testObj.getMoveTree();
        boardManager = moveTree.getGameEngine().getBoardManager();
        boardManager.createEmptyBoard();
    }

    public Tile getTile(int index) {
        return boardManager.findTileByIndex(index);
    }

    public int[] calculateNodesOnLevels(int numberOfLevels) {
        int[] numberOfNodesOnLevel = new int[numberOfLevels];
        for(Node<PositionState, Move<? extends Hop>> node : testObj.getMoveTree().getNodes()) {
            numberOfNodesOnLevel[node.getLevel()]++;
        }
        return numberOfNodesOnLevel;
    }

    @Test
    public void calculateTree_oneLevelDeep() {
        boardManager.addWhitePawn(28);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(1);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(3, testObj.getMoveTree().getNodes().size());
        for(Node<PositionState, Move<? extends Hop>> node : testObj.getMoveTree().getCurrentNode().getChildren()) {
            assertEquals(getTile(28), node.getCondition().getMovingPiece().getPosition());
            assertNotEquals(0, node.getState().getRewardFunctionOutcome());
        }
    }

    @Test
    public void calculateTree_twoLevelsDeep() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(8);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(7, testObj.getMoveTree().getNodes().size());
        for(Node<PositionState, Move<? extends Hop>> node : testObj.getMoveTree().getNodes()) {
            if (moveTree.getCurrentNode() != moveTree.getRoot()) {
                assertNotEquals(0, node.getState().getRewardFunctionOutcome());
            }
            if (node.getLevel() == 2) {
                assertEquals(getTile(8), node.getCondition().getMovingPiece().getPosition());
            }
        }
    }

    @Test
    public void calculateTree_sixLevelsDeep() {
        boardManager.addWhitePawn(46);
        boardManager.addBlackPawn(3);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree();

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());
        assertEquals(52, testObj.getMoveTree().getNodes().size());

        int[] numberOfNodesOnLevel = calculateNodesOnLevels(7);

        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(1, numberOfNodesOnLevel[1]);
        assertEquals(2, numberOfNodesOnLevel[2]);
        assertEquals(4, numberOfNodesOnLevel[3]);
        assertEquals(8, numberOfNodesOnLevel[4]);
        assertEquals(12, numberOfNodesOnLevel[5]);
        assertEquals(24, numberOfNodesOnLevel[6]);
    }

    @Test
    public void calculateTree_withCaptureInTheTree() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(19);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        int[] numberOfNodesOnLevel = calculateNodesOnLevels(3);

        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(2, numberOfNodesOnLevel[1]);
        assertEquals(3, numberOfNodesOnLevel[2]);
    }

    @Test
    public void calculateTree_withPossibilityOfWinningInTheTree() {
        Piece whitePiece = boardManager.addWhitePawn(28);
        Piece blackPiece = boardManager.addBlackPawn(19);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(3);
        testObj.calculateTree();

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(23)));
        Move<Capture> blackMove = new Move<>(blackPiece);
        blackMove.addHop(new Capture(blackPiece.getPosition(), getTile(28), whitePiece));

        testObj.getMoveTree().moveDown(whiteMove);
        testObj.getMoveTree().moveDown(blackMove);

        assertEquals(0, moveTree.getCurrentNode().getChildren().size());
        assertEquals(GameEngine.GameState.WON_BY_BLACK, moveTree.getGameEngine().getGameState());
        assertEquals(GameEngine.GameState.WON_BY_BLACK, moveTree.getCurrentNode().getState().getGameState());
        assertEquals(-100, moveTree.getCurrentNode().getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void calculateTree_withPossibilityOfDrawingInTheTree() {
        Piece whitePiece = boardManager.addWhiteQueen(40);
        boardManager.addBlackPawn(5);
        moveTree.getGameEngine().setIsWhiteToMove(true);
        DrawArbiter drawArbiter = new DrawArbiter();
        drawArbiter.setDrawCounter(1);
        drawArbiter.setDrawConditions(DrawArbiter.DrawConditions.TWO_VS_ONE);
        moveTree.getGameEngine().setDrawArbiter(drawArbiter);
        moveTree.saveGameStateToNode();

        testObj.setDepth(3);
        testObj.calculateTree();

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(34)));

        testObj.getMoveTree().moveDown(whiteMove);

        assertEquals(GameEngine.GameState.DRAWN, moveTree.getGameEngine().getGameState());
        assertEquals(GameEngine.GameState.DRAWN, moveTree.getCurrentNode().getState().getGameState());
        assertEquals(0, moveTree.getCurrentNode().getState().getDrawCounter());
        assertEquals(0, moveTree.getCurrentNode().getState().getRewardFunctionOutcome(), 0);

        int[] numberOfNodesOnLevel = calculateNodesOnLevels(4);

        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(11, numberOfNodesOnLevel[1]);
        assertEquals(0, numberOfNodesOnLevel[2]);
        assertEquals(0, numberOfNodesOnLevel[3]);
    }

    @Test
    public void calculateTreeLevel() throws Exception {
        Piece whitePiece = boardManager.addWhitePawn(32);
        boardManager.addBlackPawn(17);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(27)));

        testObj.updateTreeAfterMove(whiteMove);

        assertEquals(3, moveTree.getNodes().size());

        int[] numberOfNodesOnLevel = calculateNodesOnLevels(3);

        assertEquals(0, moveTree.getCurrentNode().getLevel());
        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(2, numberOfNodesOnLevel[1]);
        assertEquals(0, numberOfNodesOnLevel[2]);

        testObj.calculateTreeLevel(2);
        numberOfNodesOnLevel = calculateNodesOnLevels(3);

        assertEquals(5, moveTree.getNodes().size());
        assertEquals(2, numberOfNodesOnLevel[2]);
    }

    @Test (expected = ChosenLevelAlreadyCalculatedException.class)
    public void calculateTreeLevel_levelAlreadyCalculated() throws ChosenLevelAlreadyCalculatedException {
        Piece whitePiece = boardManager.addWhitePawn(32);
        boardManager.addBlackPawn(17);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(27)));

        testObj.updateTreeAfterMove(whiteMove);
        testObj.calculateTreeLevel(1);
    }

    @Test
    public void calculateTreeLevel_fromCurrentNodeDifferentThanRoot() throws Exception {
        Piece whitePiece = boardManager.addWhitePawn(32);
        Piece blackPiece = boardManager.addBlackPawn(17);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(27)));

        testObj.updateTreeAfterMove(whiteMove);

        Move<Hop> blackMove = new Move<>(blackPiece);
        blackMove.addHop(new Hop(blackPiece.getPosition(), getTile(21)));
        moveTree.moveDown(blackMove);

        moveTree.returnToRoot();
        testObj.calculateTreeLevel(2);

        int[] numberOfNodesOnLevel = calculateNodesOnLevels(3);

        assertEquals(0, moveTree.getCurrentNode().getLevel());
        assertEquals(1, numberOfNodesOnLevel[0]);
        assertEquals(2, numberOfNodesOnLevel[1]);
        assertEquals(2, numberOfNodesOnLevel[2]);
    }

    @Test
    public void assessPosition_twoLevels_whiteToDecide() {
        boardManager.addWhitePawn(6);
        boardManager.addBlackPawn(33);
        boardManager.addBlackPawn(42);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree();

        Node<PositionState, Move<? extends Hop>> childNode = moveTree.getCurrentNode().getChildren().get(0);
        assertEquals(-1, childNode.getState().getRewardFunctionOutcome(), 0);
        assertEquals(-1, moveTree.getCurrentNode().getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void assessPosition_twoLevels_blackToDecide() {
        boardManager.addBlackPawn(45);
        boardManager.addWhitePawn(8);
        boardManager.addWhitePawn(23);
        moveTree.getGameEngine().setIsWhiteToMove(false);

        testObj.setDepth(2);
        testObj.calculateTree();

        Node<PositionState, Move<? extends Hop>> childNode = moveTree.getCurrentNode().getChildren().get(0);
        assertEquals(1, childNode.getState().getRewardFunctionOutcome(), 0);
        assertEquals(1, moveTree.getCurrentNode().getState().getRewardFunctionOutcome(), 0);
    }

}
