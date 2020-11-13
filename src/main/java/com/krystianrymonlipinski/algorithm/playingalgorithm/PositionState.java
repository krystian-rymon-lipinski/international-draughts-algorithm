package com.krystianrymonlipinski.algorithm.playingalgorithm;

import draughts.library.managers.DrawArbiter;
import draughts.library.managers.GameEngine;

public class PositionState {

    private float rewardFunctionOutcome;
    private GameEngine.GameState gameState;
    private DrawArbiter.DrawConditions drawConditions;
    private int drawCounter;
    private float alpha = RewardCalculator.FITNESS_FUNCTION_VALUE_BLACK_WON;
    private float beta = RewardCalculator.FITNESS_FUNCTION_VALUE_WHITE_WON;

    public PositionState(GameEngine gameEngine) {
        this.gameState = gameEngine.getGameState();
        this.drawConditions = gameEngine.getDrawArbiter().getDrawConditions();
        this.drawCounter = gameEngine.getDrawArbiter().getDrawCounter();
    }

    public float getRewardFunctionOutcome() {
        return rewardFunctionOutcome;
    }

    public void setRewardFunctionOutcome(float rewardFunctionOutcome) {
        this.rewardFunctionOutcome = rewardFunctionOutcome;
    }

    public GameEngine.GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameEngine.GameState gameState) {
        this.gameState = gameState;
    }

    public DrawArbiter.DrawConditions getDrawConditions() {
        return drawConditions;
    }

    public void setDrawConditions(DrawArbiter.DrawConditions drawConditions) {
        this.drawConditions = drawConditions;
    }

    public int getDrawCounter() {
        return drawCounter;
    }

    public void setDrawCounter(int drawCounter) {
        this.drawCounter = drawCounter;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }
}
