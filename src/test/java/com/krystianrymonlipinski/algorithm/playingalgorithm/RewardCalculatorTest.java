package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.BoardManager;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RewardCalculatorTest {

    private RewardCalculator testObj;
    private GameEngine gameEngine;

    @Before
    public void setup() {
        gameEngine = new GameEngine();
        testObj = new RewardCalculator(gameEngine.getBoardManager());
    }

    @Test
    public void findBestChild_maximizingNode() {
        Node<PositionState, Move<? extends Hop>> root = new Node<>();
        root.setState(new PositionState(gameEngine));

        Node<PositionState, Move<? extends Hop>> child1 = new Node<>();
        child1.setState(new PositionState(gameEngine));
        child1.getState().setRewardFunctionOutcome(5.2f);

        Node<PositionState, Move<? extends Hop>> child2 = new Node<>();
        child2.setState(new PositionState(gameEngine));
        child2.getState().setRewardFunctionOutcome(-2.1f);

        Node<PositionState, Move<? extends Hop>> child3 = new Node<>();
        child3.setState(new PositionState(gameEngine));
        child3.getState().setRewardFunctionOutcome(0.98f);

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        testObj.findBestChild(root, true);
        assertEquals(5.2f, root.getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void findBestChild_minimizingNode() {
        Node<PositionState, Move<? extends Hop>> root = new Node<>();
        root.setState(new PositionState(gameEngine));

        Node<PositionState, Move<? extends Hop>> child1 = new Node<>();
        child1.setState(new PositionState(gameEngine));
        child1.getState().setRewardFunctionOutcome(3.1f);

        Node<PositionState, Move<? extends Hop>> child2 = new Node<>();
        child2.setState(new PositionState(gameEngine));
        child2.getState().setRewardFunctionOutcome(0.11f);

        Node<PositionState, Move<? extends Hop>> child3 = new Node<>();
        child3.setState(new PositionState(gameEngine));
        child3.getState().setRewardFunctionOutcome(-4.91f);

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        testObj.findBestChild(root, false);
        assertEquals(-4.91f, root.getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void calculatePosition_noWeights_withQueens() {
        BoardManager boardManager = gameEngine.getBoardManager();
        boardManager.createEmptyBoard();
        boardManager.addWhiteQueen(12);
        boardManager.addWhiteQueen(35);
        boardManager.addBlackQueen(19);

        double whiteValue = testObj.calculatePieces(boardManager.getWhitePieces(), null);
        double blackValue = testObj.calculatePieces(boardManager.getBlackPieces(), null);

        assertEquals(6.24, whiteValue, 0.02); //minor inconsistencies possible
        assertEquals(3.12, blackValue, 0.02);
    }

    @Test
    public void calculatePosition_noWeights_withPawnRows() {
        BoardManager boardManager = gameEngine.getBoardManager();
        boardManager.createEmptyBoard();
        boardManager.addWhitePawn(10);
        boardManager.addWhitePawn(11);
        boardManager.addWhitePawn(22);
        boardManager.addBlackPawn(24);
        boardManager.addBlackPawn(33);
        boardManager.addBlackPawn(41);

        double whiteValue = testObj.calculatePieces(boardManager.getWhitePieces(), null);
        double blackValue = testObj.calculatePieces(boardManager.getBlackPieces(), null);

        assertEquals(3.88, whiteValue, 0.02); //minor inconsistencies possible
        assertEquals(3.73, blackValue, 0.02);
    }

    @Test
    public void calculatePosition_noWeights_withPawnsRows_withPawnStructures() {
        BoardManager boardManager = gameEngine.getBoardManager();
        boardManager.createEmptyBoard();
        boardManager.addWhitePawn(12);
        boardManager.addWhitePawn(13);
        boardManager.addWhitePawn(18);
        boardManager.addBlackPawn(32);
        boardManager.addBlackPawn(33);
        boardManager.addBlackPawn(38);

        double whiteValue = testObj.calculatePieces(boardManager.getWhitePieces(), null);
        double blackValue = testObj.calculatePieces(boardManager.getBlackPieces(), null);

        assertEquals(3.86, whiteValue, 0.02);
        assertEquals(3.77, blackValue, 0.02);
    }
}
