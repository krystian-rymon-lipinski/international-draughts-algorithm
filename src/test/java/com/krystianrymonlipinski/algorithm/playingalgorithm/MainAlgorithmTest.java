package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.BoardManager;
import draughts.library.managers.DrawArbiter;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

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
        testObj.setUsingAlphaBeta(false);
        moveTree = testObj.getMoveTree();
        boardManager = moveTree.getGameEngine().getBoardManager();
        boardManager.createEmptyBoard();
    }

    public Tile getTile(int index) {
        return boardManager.findTileByIndex(index);
    }

    public ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> countTreeNodes(int numberOfLevels) {
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels = new ArrayList<>();
        for (int i=0; i<=numberOfLevels; i++) { //include root as level 0
            nodesOnLevels.add(new ArrayList<>());
        }
        iterate(nodesOnLevels);
        return nodesOnLevels;
    }

    public void iterate(ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels) {
        for (Node<PositionState, Move<? extends Hop>> child : moveTree.getCurrentNode().getChildren()) {
            moveTree.moveDown(child.getCondition());
            iterate(nodesOnLevels);
            moveTree.moveUp();
        }

        int levelDifference = moveTree.getCurrentNode().getLevel() - moveTree.getRoot().getLevel();
        nodesOnLevels.get(levelDifference).add(moveTree.getCurrentNode());
    }

    @Test
    public void calculateTree_oneLevelDeep() {
        Piece whitePawn = boardManager.addWhitePawn(28);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(1);
        testObj.calculateTree(null);

        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(1);


        assertEquals(testObj.getMoveTree().getRoot(), testObj.getMoveTree().getCurrentNode());

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(2, nodesOnLevels.get(1).size());

        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(1)) {
            assertEquals(whitePawn, node.getCondition().getMovingPiece());
            assertNotEquals(0, node.getState().getRewardFunctionOutcome());
        }
    }

    @Test
    public void calculateTree_twoLevelsDeep() {
        boardManager.addWhitePawn(28);
        Piece blackPawn = boardManager.addBlackPawn(8);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree(null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(2);

        assertEquals(testObj.getMoveTree().getRoot(), testObj.getMoveTree().getCurrentNode());

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(2, nodesOnLevels.get(1).size());
        assertEquals(4, nodesOnLevels.get(2).size());
        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(2)) {
            assertEquals(blackPawn, node.getCondition().getMovingPiece());
            assertNotEquals(0, node.getState().getRewardFunctionOutcome());
        }
    }

    @Test
    public void calculateTree_sixLevelsDeep() {
        boardManager.addWhitePawn(46);
        boardManager.addBlackPawn(3);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree(null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(6);

        assertEquals(testObj.getMoveTree().getRoot().getIndex(), testObj.getMoveTree().getCurrentNode().getIndex());

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(1, nodesOnLevels.get(1).size());
        assertEquals(2, nodesOnLevels.get(2).size());
        assertEquals(4, nodesOnLevels.get(3).size());
        assertEquals(8, nodesOnLevels.get(4).size());
        assertEquals(12, nodesOnLevels.get(5).size());
        assertEquals(24, nodesOnLevels.get(6).size());
    }

    @Test
    public void calculateTree_withCaptureInTheTree() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(19);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree(null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(2);

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(2, nodesOnLevels.get(1).size());
        assertEquals(3, nodesOnLevels.get(2).size());

        int numberOfNodesWithCapture = 0;
        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(2)) {
            if (node.getCondition().isCapture()) numberOfNodesWithCapture++;
        }
        assertEquals(1, numberOfNodesWithCapture);
    }

    @Test
    public void calculateTree_withPossibilityOfWinningInTheTree() {
        boardManager.addWhitePawn(28);
        boardManager.addBlackPawn(19);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(3);
        testObj.calculateTree(null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(3);

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(2, nodesOnLevels.get(1).size());
        assertEquals(3, nodesOnLevels.get(2).size());
        assertEquals(4, nodesOnLevels.get(3).size());

        int numberOfNodesWithGameEnded = 0;
        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(2)) {
            if (node.getState().getGameState() != GameEngine.GameState.RUNNING) {
                assertEquals(0, node.getChildren().size());
                assertEquals(GameEngine.GameState.WON_BY_BLACK, node.getState().getGameState());
                assertEquals(-100, node.getState().getRewardFunctionOutcome(), 0);
                numberOfNodesWithGameEnded++;
            }
        }
        assertEquals(1, numberOfNodesWithGameEnded);
    }

    @Test
    public void calculateTree_withPossibilityOfDrawingInTheTree() {
        boardManager.addWhiteQueen(40);
        boardManager.addBlackPawn(5);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        DrawArbiter drawArbiter = new DrawArbiter();
        drawArbiter.setDrawCounter(1);
        drawArbiter.setDrawConditions(DrawArbiter.DrawConditions.TWO_VS_ONE);

        moveTree.getGameEngine().setDrawArbiter(drawArbiter);
        moveTree.saveGameStateToNode();

        testObj.setDepth(3);
        testObj.calculateTree(null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(3);

        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(11, nodesOnLevels.get(1).size());
        assertEquals(0, nodesOnLevels.get(2).size());
        assertEquals(0, nodesOnLevels.get(3).size());

        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(1)) {
            assertEquals(GameEngine.GameState.DRAWN, node.getState().getGameState());
            assertEquals(0, node.getState().getDrawCounter());
            assertEquals(0, node.getState().getRewardFunctionOutcome(), 0);

        }
    }

    @Test
    public void calculateTree_withPromotionsInTheTree() {
        boardManager.addWhitePawn(7);
        boardManager.addWhitePawn(8);
        boardManager.addBlackPawn(42);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree(null);

        assertEquals(2, moveTree.getGameEngine().getBoardManager().getWhitePieces().size());
        assertEquals(1, moveTree.getGameEngine().getBoardManager().getBlackPieces().size());

        moveTree.moveDown(moveTree.getCurrentNode().getChildren().get(0).getCondition());

        assertTrue(moveTree.getCurrentNode().getCondition().isPromotion());
        assertEquals(2, moveTree.getGameEngine().getBoardManager().getWhitePieces().size());

        moveTree.moveDown(moveTree.getCurrentNode().getChildren().get(0).getCondition());

        assertTrue(moveTree.getCurrentNode().getCondition().isPromotion());
        assertEquals(1, moveTree.getGameEngine().getBoardManager().getBlackPieces().size());
    }

    @Test
    public void calculateTree_usingAlphaBeta() {
        boardManager.addWhitePawn(13);
        boardManager.addWhitePawn(38);
        boardManager.addBlackPawn(28);
        boardManager.addBlackPawn(29);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.setUsingAlphaBeta(true);

        assertEquals(-100, moveTree.getRoot().getState().getAlpha(), 0);
        assertEquals(100, moveTree.getRoot().getState().getBeta(), 0);

        testObj.calculateTree(null);

        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels_withAlphaBeta =
                countTreeNodes(2);

        assertEquals(10, nodesOnLevels_withAlphaBeta.get(2).size()); //11 without alpha-beta
    }


    @Test
    public void calculateTreeLevel() {
        Piece whitePiece = boardManager.addWhitePawn(32);
        boardManager.addBlackPawn(17);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(2);
        testObj.calculateTree(null);

        Move<Hop> whiteMove = new Move<>(whitePiece);
        whiteMove.addHop(new Hop(whitePiece.getPosition(), getTile(27)));
        testObj.getMoveTree().moveDown(whiteMove);

        testObj.updateTreeAfterMove();
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels =
                countTreeNodes(1);

        assertEquals(1, moveTree.getCurrentNode().getLevel());
        assertEquals(1, moveTree.getRoot().getLevel());
        assertEquals(1, nodesOnLevels.get(0).size());
        assertEquals(2, nodesOnLevels.get(1).size());

        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels.get(1)) {
            assertEquals(2, node.getLevel());
        }

        testObj.calculateNextTreeLevel(3, null);
        ArrayList<ArrayList<Node<PositionState, Move<? extends Hop>>>> nodesOnLevels_withNewLevel =
                countTreeNodes(2);

        assertEquals(1, nodesOnLevels_withNewLevel.get(0).size());
        assertEquals(2, nodesOnLevels_withNewLevel.get(1).size());
        assertEquals(2, nodesOnLevels_withNewLevel.get(2).size());
        assertEquals(moveTree.getRoot(), moveTree.getCurrentNode());

        for(Node<PositionState, Move<? extends Hop>> node : nodesOnLevels_withNewLevel.get(2)) {
            assertEquals(3, node.getLevel());
        }
    }

    @Test
    public void findBestMove_promotionOnRightTile() {
        boardManager.createEmptyBoard();

        boardManager.addWhitePawn(9);
        boardManager.addBlackPawn(12);
        boardManager.addBlackPawn(26);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree(null);
        assertNotEquals(0, moveTree.getCurrentNode().getState().getRewardFunctionOutcome());

        Move<? extends Hop> bestMove = testObj.findBestMove();
        testObj.getMoveTree().moveDown(bestMove);

        assertEquals(getTile(4), bestMove.getMovingPiece().getPosition());
        assertEquals(getTile(4), moveTree.getCurrentNode().getCondition().getMovingPiece().getPosition());
    }

    @Test
    public void findBestMove_pawnCaptureTactic() {
        boardManager.createEmptyBoard();

        boardManager.addWhitePawn(33);
        boardManager.addWhitePawn(36);
        boardManager.addWhitePawn(38);
        boardManager.addWhitePawn(39);
        boardManager.addWhitePawn(44);
        boardManager.addBlackPawn(16);
        boardManager.addBlackPawn(18);
        boardManager.addBlackPawn(21);
        boardManager.addBlackPawn(26);
        moveTree.getGameEngine().setIsWhiteToMove(false);

        testObj.setDepth(4);
        testObj.calculateTree(null);

        Move<? extends Hop> bestMove = testObj.findBestMove();
        testObj.getMoveTree().moveDown(bestMove);

        assertEquals(getTile(31), bestMove.getMovingPiece().getPosition());
        assertEquals(getTile(31), moveTree.getCurrentNode().getCondition().getMovingPiece().getPosition());

    }

    @Test
    public void findBestMove_preventPawnCaptureTactic() {
        boardManager.createEmptyBoard();

        boardManager.addWhitePawn(33);
        boardManager.addWhitePawn(36);
        boardManager.addWhitePawn(38);
        boardManager.addWhitePawn(39);
        boardManager.addWhitePawn(44);
        boardManager.addBlackPawn(16);
        boardManager.addBlackPawn(18);
        boardManager.addBlackPawn(21);
        boardManager.addBlackPawn(26);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(4);
        testObj.calculateTree(null);

        Move<? extends Hop> bestMove = testObj.findBestMove();
        testObj.getMoveTree().moveDown(bestMove);

        assertEquals(getTile(32), bestMove.getMovingPiece().getPosition());
        assertEquals(getTile(32), moveTree.getCurrentNode().getCondition().getMovingPiece().getPosition());
    }

    @Test
    public void findBestMove_preventPawnCaptureTactic_searchDeeper() {
        boardManager.createEmptyBoard();

        boardManager.addWhitePawn(33);
        boardManager.addWhitePawn(36);
        boardManager.addWhitePawn(38);
        boardManager.addWhitePawn(39);
        boardManager.addWhitePawn(44);
        boardManager.addBlackPawn(16);
        boardManager.addBlackPawn(18);
        boardManager.addBlackPawn(21);
        boardManager.addBlackPawn(26);
        moveTree.getGameEngine().setIsWhiteToMove(true);

        testObj.setDepth(6);
        testObj.calculateTree(null);

        Move<? extends Hop> bestMove = testObj.findBestMove();
        testObj.getMoveTree().moveDown(bestMove);

        assertEquals(getTile(31), bestMove.getMovingPiece().getPosition());
        assertEquals(getTile(31), moveTree.getCurrentNode().getCondition().getMovingPiece().getPosition());

        testObj.getMoveTree().moveDown(moveTree.getCurrentNode().getChildren().get(0).getCondition());

        Move<? extends Hop> nextBestMove = testObj.findBestMove();
        testObj.getMoveTree().moveDown(nextBestMove);

        assertEquals(getTile(32), nextBestMove.getMovingPiece().getPosition());
        assertEquals(getTile(32), moveTree.getCurrentNode().getCondition().getMovingPiece().getPosition());
    }

}
