package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class RewardCalculator {

    private final BoardManager boardManager;

    public RewardCalculator(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    public void assessPosition(Node<PositionState, Move<? extends Hop>> node, boolean isPlayedColorWhite) {
        double rewardOutcome = 0;

        switch (node.getState().getGameState()) {
            case RUNNING:
                rewardOutcome = assessPieces();
                break;
            case WON_BY_BLACK:
                rewardOutcome = -100;
                break;
            case WON_BY_WHITE:
                rewardOutcome = 100;
                break;
            case DRAWN:
                rewardOutcome = 0;
            default:
                break;
        }

        node.getState().setRewardFunctionOutcome(rewardOutcome);
    }

    public void findMinimumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        double minimum = Double.MAX_VALUE;

        for (Node<PositionState, Move<? extends Hop>> child : ancestor.getChildren()) {
            if (child.getState().getRewardFunctionOutcome() < minimum) {
                minimum = child.getState().getRewardFunctionOutcome();
            }
        }

        ancestor.getState().setRewardFunctionOutcome(minimum);
    }

    public void findMaximumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        double maximum = -Double.MAX_VALUE;

        for (Node<PositionState, Move<? extends Hop>> child : ancestor.getChildren()) {
            if (child.getState().getRewardFunctionOutcome() > maximum) {
                maximum = child.getState().getRewardFunctionOutcome();
            }
        }

        ancestor.getState().setRewardFunctionOutcome(maximum);
    }

    public double assessPieces() {
        double whitePiecesValue = calculateBasicPiecesValue(boardManager.getWhitePieces());
        double blackPiecesValue = calculateBasicPiecesValue(boardManager.getBlackPieces());

        return whitePiecesValue - blackPiecesValue;
    }

    public double calculateBasicPiecesValue(ArrayList<Piece> pieces) {
        double value = 0;
        for (Piece piece : pieces) {
            if (piece.isQueen()) value += calculateQueenValue();
            else                 value += 1;
        }
        return value;
    }

    private double calculateQueenValue() {
        int numberOfPieces = boardManager.getBlackPieces().size() + boardManager.getWhitePieces().size();
        return queenFunction(numberOfPieces);
    }

    private double queenFunction(int numberOfPieces) {
        return 3;
    }
}
