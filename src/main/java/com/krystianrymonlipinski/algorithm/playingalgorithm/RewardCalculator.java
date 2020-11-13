package com.krystianrymonlipinski.algorithm.playingalgorithm;

import com.krystianrymonlipinski.algorithm.geneticalgorithm.Specimen;
import com.krystianrymonlipinski.tree.model.Node;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.managers.BoardManager;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

import java.util.ArrayList;

public class RewardCalculator {

    public final static int FITNESS_FUNCTION_VALUE_WHITE_WON = 100;
    public final static int FITNESS_FUNCTION_VALUE_BLACK_WON = -100;
    public final static int FITNESS_FUNCTION_VALUE_DRAW = 0;

    private final BoardManager boardManager;
    private float fitnessFunctionValue;

    private final float[] queenValueCoefficients = {
        /* Interpolation nodes (number of pieces on the board -> value added to queenValue parameter):
            1 -> 3.25
            10 -> 2.85
            20 -> 2.75
            30 -> 2.65
            40 -> 2.25
         */
        3.20484f * (float) Math.pow(10, -7),
        -8.2084f * (float) Math.pow(10, -5),
        0.0041f,
        -0.0810f,
        3.3270f
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

    private Node<PositionState, Move<? extends Hop>> findMinimumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        float minimum = FITNESS_FUNCTION_VALUE_WHITE_WON + 1;
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

    private Node<PositionState, Move<? extends Hop>> findMaximumChild(Node<PositionState, Move<? extends Hop>> ancestor) {
        float maximum = FITNESS_FUNCTION_VALUE_BLACK_WON - 1;
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
        float whitePiecesValue = calculatePieces(boardManager.getWhitePieces(), specimen);
        float blackPiecesValue = calculatePieces(boardManager.getBlackPieces(), specimen);

        fitnessFunctionValue = whitePiecesValue - blackPiecesValue;
    }

    public float calculatePieces(ArrayList<Piece> pieces, Specimen specimen) {
        float[] fitnessValue = new float[4];
        for (Piece piece : pieces) {
            if (piece.isQueen()) fitnessValue[0] += calculateQueenValue();
            else {
                fitnessValue[1] += 1;

                int row = piece.getPosition().getRow();
                if (piece.isWhite()) {
                    fitnessValue[2] += calculatePawnRowValue(Math.abs(row-11));
                }
                else {
                    fitnessValue[2] += calculatePawnRowValue(row);
                }

                fitnessValue[3] += calculatePawnStructureValue(piece);
            }
        }
        if (specimen == null) {
            return fitnessValue[0] + fitnessValue[1] + fitnessValue[2] + fitnessValue[3];
        } else {
            return  specimen.getQueensWeight() * fitnessValue[0] +
                    specimen.getPawnsWeight() * fitnessValue[1] +
                    specimen.getPawnsPositionsWeight() * fitnessValue[2] +
                    specimen.getPawnsStructuresWeight() * fitnessValue[3];
        }
    }

    private float queenFunctionBasic(int numberOfPieces) {
        return 3;
    }

    private float calculateQueenValue() {
        int numberOfPieces = boardManager.getBlackPieces().size() + boardManager.getWhitePieces().size();

        return  queenValueCoefficients[0] * (float) Math.pow(numberOfPieces, 4) +
                queenValueCoefficients[1] * (float) Math.pow(numberOfPieces, 3) +
                queenValueCoefficients[2] * (float) Math.pow(numberOfPieces, 2) +
                queenValueCoefficients[3] * (float) numberOfPieces +
                queenValueCoefficients[4];

    }

    private float calculatePawnRowValue(int row) {
        return  (float) (Math.pow(2, (float) row/2) - 1) / 50;
    }

    private float calculatePawnStructureValue(Piece piece) {
        int friendlyNeighbours = 0;

        if (piece.getPosition().getColumn() == 1) {
            friendlyNeighbours++;
        } else {
            if (piece.getPosition().getRow() > 1) {
                Tile upLeftTile = piece.findTarget(Piece.MoveDirection.UP_LEFT, boardManager.getBoard(), 1);
                if (piece.isTileOccupiedBySameColor(upLeftTile)) friendlyNeighbours++;
            }
            if (piece.getPosition().getRow() < 10) {
                Tile downLeftTile = piece.findTarget(Piece.MoveDirection.DOWN_LEFT, boardManager.getBoard(), 1);
                if (piece.isTileOccupiedBySameColor(downLeftTile)) friendlyNeighbours++;
            }
        }

        if (piece.getPosition().getColumn() == 10) {
            friendlyNeighbours++;
        } else {
            if (piece.getPosition().getRow() > 1) {
                Tile upRightTile = piece.findTarget(Piece.MoveDirection.UP_RIGHT, boardManager.getBoard(), 1);
                if (piece.isTileOccupiedBySameColor(upRightTile)) friendlyNeighbours++;
            }
            if (piece.getPosition().getRow() < 10) {
                Tile downRightTile = piece.findTarget(Piece.MoveDirection.DOWN_RIGHT, boardManager.getBoard(), 1);
                if (piece.isTileOccupiedBySameColor(downRightTile)) friendlyNeighbours++;
            }
        }

        return (float) friendlyNeighbours*friendlyNeighbours/100;
    }
}
