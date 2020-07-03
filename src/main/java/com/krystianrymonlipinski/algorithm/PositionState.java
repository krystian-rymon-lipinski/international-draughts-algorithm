package com.krystianrymonlipinski.algorithm;

import draughts.library.managers.GameEngine;

public class PositionState {

    private int rewardFunctionOutcome;
    private GameEngine.GameState gameState;

    public PositionState(GameEngine.GameState gameState) {
        this.gameState = gameState;
    }

    public int getRewardFunctionOutcome() {
        return rewardFunctionOutcome;
    }

    public void setRewardFunctionOutcome(int rewardFunctionOutcome) {
        this.rewardFunctionOutcome = rewardFunctionOutcome;
    }

    public GameEngine.GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameEngine.GameState gameState) {
        this.gameState = gameState;
    }
}
