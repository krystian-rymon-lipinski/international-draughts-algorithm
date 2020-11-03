package com.krystianrymonlipinski.algorithm.playingalgorithm;

import draughts.library.managers.DrawArbiter;
import draughts.library.managers.GameEngine;

public class PositionState {

    private double rewardFunctionOutcome;
    private GameEngine.GameState gameState;
    private DrawArbiter.DrawConditions drawConditions;
    private int drawCounter;
    private double alpha = RewardCalculator.FITNESS_FUNCTION_VALUE_BLACK_WON;
    private double beta = RewardCalculator.FITNESS_FUNCTION_VALUE_WHITE_WON;

    public PositionState(GameEngine gameEngine) {
        this.gameState = gameEngine.getGameState();
        this.drawConditions = gameEngine.getDrawArbiter().getDrawConditions();
        this.drawCounter = gameEngine.getDrawArbiter().getDrawCounter();
    }

    public double getRewardFunctionOutcome() {
        return rewardFunctionOutcome;
    }

    public void setRewardFunctionOutcome(double rewardFunctionOutcome) {
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

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
