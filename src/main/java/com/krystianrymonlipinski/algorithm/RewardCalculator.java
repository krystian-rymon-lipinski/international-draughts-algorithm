package com.krystianrymonlipinski.algorithm;

import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class RewardCalculator {

    private final BoardManager boardManager;
    private boolean isPlayedColorWhite;

    public RewardCalculator(BoardManager boardManager, boolean isPlayedColorWhite) {
        this.boardManager = boardManager;
        this.isPlayedColorWhite = isPlayedColorWhite;
    }

    public void assessPosition(Node<PositionState, Move<? extends Hop>> node) {
        double rewardOutcome = 0;

        double whitePiecesValue = assessPieces(boardManager.getWhitePieces());
        double blackPiecesValue = assessPieces(boardManager.getBlackPieces());

        rewardOutcome = whitePiecesValue - blackPiecesValue;

        if (!isPlayedColorWhite) {
            rewardOutcome *= -1;
        }

        node.getState().setRewardFunctionOutcome(rewardOutcome);
    }

    public double assessPieces(ArrayList<Piece> pieces) {
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
