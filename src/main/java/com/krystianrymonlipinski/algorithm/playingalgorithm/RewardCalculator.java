package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.algorithm.geneticalgorithm.Specimen;
import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class RewardCalculator {

    public final static int FITNESS_FUNCTION_VALUE_WHITE_WON = 100;
    public final static int FITNESS_FUNCTION_VALUE_BLACK_WON = -100;
    public final static int FITNESS_FUNCTION_VALUE_DRAW = 0;

    private final BoardManager boardManager;
    private double fitnessFunctionValue;

    private final double[] queenValueCoefficients = {
        3.20484 * Math.pow(10, -7),
        -8.2084 * Math.pow(10, -5),
        0.0041,
        -0.0810,
        3.3270
    };
    private final double[] pawnRowValueCoefficients = {
        -0.0117,
        0.28,
        -2.1283,
        5.23
    };

    public RewardCalculator(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.fitnessFunctionValue = 0;
    }

    public void assessPosition(Node<PositionState, Move<? extends Hop>> node, Specimen specimen) {
        switch (node.getState().getGameState()) {
            case RUNNING:
                calculatePosition(specimen);
                break;
            case WON_BY_BLACK:
                fitnessFunctionValue = FITNESS_FUNCTION_VALUE_BLACK_WON;
                break;
            case WON_BY_WHITE:
                fitnessFunctionValue = FITNESS_FUNCTION_VALUE_WHITE_WON;
                break;
            case DRAWN:
                fitnessFunctionValue = FITNESS_FUNCTION_VALUE_DRAW;
            default:
                break;
        }

        node.getState().setRewardFunctionOutcome(fitnessFunctionValue);
        fitnessFunctionValue = 0; //to calculate next nodes without bias
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

    public void calculatePosition(Specimen specimen) {
        double whitePiecesValue = calculatePieces(boardManager.getWhitePieces(), specimen);
        double blackPiecesValue = calculatePieces(boardManager.getBlackPieces(), specimen);

        fitnessFunctionValue = whitePiecesValue - blackPiecesValue;
    }

    public double calculatePieces(ArrayList<Piece> pieces, Specimen specimen) {
        double[] fitnessValue = new double[3];
        for (Piece piece : pieces) {
            if (piece.isQueen()) fitnessValue[0] += calculateQueenValue();
            else {
                fitnessValue[1] += 1;

                int row = piece.getPosition().getRow();
                if (piece.isWhite() && row <= 5) {
                    fitnessValue[2] += calculatePawnRowValue(Math.abs(row-11));
                }
                else if (!piece.isWhite() && row >= 6) {
                    fitnessValue[2] += calculatePawnRowValue(row);
                }
            }
        }
        if (specimen == null) {
            return fitnessValue[0] + fitnessValue[1] + fitnessValue[2];
        } else {
            return  specimen.getQueensWeight() * fitnessValue[0] +
                    specimen.getPawnsWeight() + fitnessValue[1] +
                    specimen.getPawnsPositionsWeight() + fitnessValue[2];
        }
    }

    private double queenFunctionBasic(int numberOfPieces) {
        return 3;
    }

    private double calculateQueenValue() {
        int numberOfPieces = boardManager.getBlackPieces().size() + boardManager.getWhitePieces().size();

        return  queenValueCoefficients[0] * Math.pow(numberOfPieces, 4) +
                queenValueCoefficients[1] * Math.pow(numberOfPieces, 3) +
                queenValueCoefficients[2] * Math.pow(numberOfPieces, 2) +
                queenValueCoefficients[3] * numberOfPieces +
                queenValueCoefficients[4];

    }

    private double calculatePawnRowValue(int row) {
        return  pawnRowValueCoefficients[0] * Math.pow(row, 3) +
                pawnRowValueCoefficients[1] * Math.pow(row, 2) +
                pawnRowValueCoefficients[2] * Math.pow(row, 1) +
                pawnRowValueCoefficients[3];
    }
}
