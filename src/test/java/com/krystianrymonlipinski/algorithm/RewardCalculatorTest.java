package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.algorithm.playingalgorithm.PositionState;
import com.krystianrymonlipinski.algorithm.playingalgorithm.RewardCalculator;
import com.krystianrymonlipinski.tree.model.Node;
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
}
