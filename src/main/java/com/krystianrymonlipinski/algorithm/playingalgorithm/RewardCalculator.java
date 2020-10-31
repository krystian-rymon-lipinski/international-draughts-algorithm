package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.algorithm.geneticalgorithm.Specimen;
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

    public void assessPosition(Node<PositionState, Move<? extends Hop>> node, Specimen specimen) {
        double rewardOutcome = 0;

        switch (node.getState().getGameState()) {
            case RUNNING:
                rewardOutcome = calculatePosition(specimen);
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

    public Node<PositionState, Move<? extends Hop>> findBestChild(Node<PositionState, Move<? extends Hop>> ancestor,
                                                                  boolean isNodeMaximizing) {
        if (isNodeMaximizing) return findMaximumChild(ancestor);
        else                  return findMinimumChild(ancestor);
    }

    public Node<PositionState, Move<? extends Hop>> findMinimumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        double minimum = Double.MAX_VALUE;
        Node<PositionState, Move<? extends Hop>> currentMinimumChild = null;

        for (Node<PositionState, Move<? extends Hop>> child : ancestor.getChildren()) {
            if (child.getState().getRewardFunctionOutcome() < minimum) {
                minimum = child.getState().getRewardFunctionOutcome();
                currentMinimumChild = child;
            }
        }

        ancestor.getState().setRewardFunctionOutcome(minimum);
        return currentMinimumChild;
    }

    public Node<PositionState, Move<? extends Hop>> findMaximumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        double maximum = -Double.MAX_VALUE;
        Node<PositionState, Move<? extends Hop>> currentMaximumChild = null;

        for (Node<PositionState, Move<? extends Hop>> child : ancestor.getChildren()) {
            if (child.getState().getRewardFunctionOutcome() > maximum) {
                maximum = child.getState().getRewardFunctionOutcome();
                currentMaximumChild = child;
            }
        }

        ancestor.getState().setRewardFunctionOutcome(maximum);
        return currentMaximumChild;
    }

    public double calculatePosition(Specimen specimen) {
        double whitePiecesValue = calculatePieces(boardManager.getWhitePieces(), specimen);
        double blackPiecesValue = calculatePieces(boardManager.getBlackPieces(), specimen);

        return whitePiecesValue - blackPiecesValue;
    }

    public double calculatePieces(ArrayList<Piece> pieces, Specimen specimen) {
        double[] fitnessValue = new double[3];
        for (Piece piece : pieces) {
            if (piece.isQueen()) fitnessValue[0] += calculateQueenValue();
            else {
                fitnessValue[1] += 1;
                fitnessValue[2] += pawnRowFunction(piece);
            }
        }
        if (specimen == null) {
            return fitnessValue[0] + fitnessValue[1] + fitnessValue[2];
        } else {
            return  specimen.getQueensParameter() * fitnessValue[0] +
                    specimen.getPawnsParameter() + fitnessValue[1] +
                    specimen.getPawnsPositionParameter() + fitnessValue[2];
        }
    }

    private double queenFunctionBasic(int numberOfPieces) {
        return 3;
    }

    private double calculateQueenValue() {
        int numberOfPieces = boardManager.getBlackPieces().size() + boardManager.getWhitePieces().size();

        double a4 = 3.20484 * Math.pow(10, -7);
        double a3 = -8.2084 * Math.pow(10, -5);
        double a2 = 0.0041;
        double a1 = -0.0810;
        double a0 = 3.3270;
        return a4 * Math.pow(numberOfPieces, 4) +
               a3 * Math.pow(numberOfPieces, 3) +
               a2 * Math.pow(numberOfPieces, 2) +
                a1 * numberOfPieces +
                a0;

    }

    private double pawnRowFunction(Piece piece) {
        int row = piece.getPosition().getRow();
        if (piece.isWhite()) {
            if (row >=6) return 1; //white side of the board
            else         return Math.pow( (Math.abs(row-11) - 5), 0.1666);
        }
        else {
            if (row <=5) return 1; //black side of the board
            else         return Math.pow( row-5, 0.1666);
        }
    }
}
