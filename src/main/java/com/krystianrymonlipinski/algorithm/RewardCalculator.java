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
                rewardOutcome = assessPieces(isPlayedColorWhite);
                break;
            case WON_BY_BLACK:
                if (isPlayedColorWhite) rewardOutcome = -100;
                else                    rewardOutcome = 100;
                break;
            case WON_BY_WHITE:
                if (isPlayedColorWhite) rewardOutcome = 100;
                else                    rewardOutcome = -100;
                break;
            case DRAWN:
                rewardOutcome = 0;
            default:
                break;
        }

        node.getState().setRewardFunctionOutcome(rewardOutcome);

    }

    public double assessPieces(boolean isPlayedColorWhite) {
        double rewardOutcome = 0;
        double whitePiecesValue = calculateBasicPiecesValue(boardManager.getWhitePieces());
        double blackPiecesValue = calculateBasicPiecesValue(boardManager.getBlackPieces());

        rewardOutcome = whitePiecesValue - blackPiecesValue;

        if (!isPlayedColorWhite) {
            rewardOutcome *= -1;
        }
        return rewardOutcome;
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
