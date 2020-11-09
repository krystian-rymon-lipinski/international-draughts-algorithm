package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
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
        child1.getState().setRewardFunctionOutcome(5.2);

        Node<PositionState, Move<? extends Hop>> child2 = new Node<>();
        child2.setState(new PositionState(gameEngine));
        child2.getState().setRewardFunctionOutcome(-2.1);

        Node<PositionState, Move<? extends Hop>> child3 = new Node<>();
        child3.setState(new PositionState(gameEngine));
        child3.getState().setRewardFunctionOutcome(0.98);

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        testObj.findBestChild(root, true);
        assertEquals(5.2, root.getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void findBestChild_minimizingNode() {
        Node<PositionState, Move<? extends Hop>> root = new Node<>();
        root.setState(new PositionState(gameEngine));

        Node<PositionState, Move<? extends Hop>> child1 = new Node<>();
        child1.setState(new PositionState(gameEngine));
        child1.getState().setRewardFunctionOutcome(3.1);

        Node<PositionState, Move<? extends Hop>> child2 = new Node<>();
        child2.setState(new PositionState(gameEngine));
        child2.getState().setRewardFunctionOutcome(0.11);

        Node<PositionState, Move<? extends Hop>> child3 = new Node<>();
        child3.setState(new PositionState(gameEngine));
        child3.getState().setRewardFunctionOutcome(-4.91);

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        testObj.findBestChild(root, false);
        assertEquals(-4.91, root.getState().getRewardFunctionOutcome(), 0);
    }

    @Test
    public void calculatePosition_noWeights_withPawnRows() {
        BoardManager boardManager = gameEngine.getBoardManager();
        boardManager.createEmptyBoard();
        boardManager.addWhitePawn(10);
        boardManager.addWhitePawn(11);
        boardManager.addWhitePawn(27);
        boardManager.addBlackPawn(24);
        boardManager.addBlackPawn(33);
        boardManager.addBlackPawn(41);

        double whiteValue = testObj.calculatePieces(boardManager.getWhitePieces(), null);
        double blackValue = testObj.calculatePieces(boardManager.getBlackPieces(), null);

        assertEquals(3.4, whiteValue, 0.05); //some inconsistencies on third power
        assertEquals(3.3, blackValue, 0.05);
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

        assertEquals(3.41, whiteValue, 0.05);
        assertEquals(3.31, blackValue, 0.05);
    }
}
