package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public class RewardCalculator {

    private final BoardManager boardManager;

    public RewardCalculator(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    public void assessPosition(Node<PositionState, Move<? extends Hop>> node) {
        int rewardOutcome = 0;


        node.getState().setRewardFunctionOutcome(rewardOutcome);
    }
}
