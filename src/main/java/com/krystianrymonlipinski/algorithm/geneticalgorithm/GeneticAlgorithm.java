package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import com.krystianrymonlipinski.algorithm.playingalgorithm.MainAlgorithm;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {

    private final static int ALGORITHM_DEPTH = 4;

    private final static int NUMBER_OF_GENERATIONS = 10;
    private final static int POPULATION_SIZE = 8;
    private final static int PARAMETER_RANGE_MIN = -1;
    private final static int PARAMETER_RANGE_MAX = 10;
    private final static int PARAMETER_RANGE = PARAMETER_RANGE_MIN + PARAMETER_RANGE_MAX;

    private final static int NUMBER_OF_BEST_TO_SELECT = 1;

    private float mutationFactor = 0.2f * PARAMETER_RANGE;

    private ArrayList<Specimen> population = new ArrayList<>();




    public ArrayList<Specimen> getPopulation() {
        return population;
    }

    public void trainAlgorithm() {
        createFirstGeneration();

        for (int i=0; i<NUMBER_OF_GENERATIONS; i++) {
            System.out.println("Tournament for generation: " + (i+1) + "/" + NUMBER_OF_GENERATIONS);
            ArrayList<Specimen> standings = playTournament_playOff();
            chooseBestSpecimens(standings);
            createChildrenByMutation();

            mutationFactor -= 0.01f * PARAMETER_RANGE;
        }

        System.out.println("Best specimen: " + population.get(0));
    }

    public void createFirstGeneration() {
        for (int i = 0; i< POPULATION_SIZE; i++) {
            population.add(new Specimen(PARAMETER_RANGE_MIN, PARAMETER_RANGE_MAX));
            System.out.println(population.get(i));
        }
    }

    public ArrayList<Specimen> playTournament_playOff() {
        Random random = new Random();
        int numberOfRounds = (int) (Math.log(POPULATION_SIZE) / Math.log(2));

        ArrayList<Specimen> standings = new ArrayList<>();

        for (int i=0; i<numberOfRounds; i++) {
            int numberOfPairsInRound = POPULATION_SIZE / (int) Math.pow(2, (i+1));

            for(int j = 0; j < numberOfPairsInRound; j++) {
                int numberOfPlayersLeft = (numberOfPairsInRound - j) * 2;

                Specimen firstPlayer = population.get(random.nextInt(numberOfPlayersLeft));
                population.remove(firstPlayer);
                Specimen secondPlayer = population.get(random.nextInt(numberOfPlayersLeft - 1));
                population.remove(secondPlayer);
                GameEngine.GameState result = playGame(firstPlayer, secondPlayer);

                if (result != GameEngine.GameState.DRAWN) {
                    if (result == GameEngine.GameState.WON_BY_WHITE) {
                        standings = adjustStandings(standings, firstPlayer, secondPlayer);
                    } else {
                        standings = adjustStandings(standings, secondPlayer, firstPlayer);
                    }
                }
                else { //draw in tournament tree
                    GameEngine.GameState rematchResult = playGame(secondPlayer, firstPlayer);

                    if (rematchResult != GameEngine.GameState.DRAWN) {
                        if (rematchResult == GameEngine.GameState.WON_BY_WHITE) {
                            standings = adjustStandings(standings, secondPlayer, firstPlayer);
                        } else {
                            standings = adjustStandings(standings, firstPlayer, secondPlayer);
                        }
                    }

                    else { //draw in rematch - create new player from both parents
                        Specimen newPlayer = createChildByCrossing(firstPlayer, secondPlayer);
                        if (new Random().nextBoolean()) adjustStandings(standings, newPlayer, firstPlayer);
                        else                            adjustStandings(standings, newPlayer, secondPlayer);
                    }
                }
            }
        }
        standings.add(0, population.get(0));
        population.remove(0);

        return standings;
    }

    public ArrayList<Specimen> adjustStandings(ArrayList<Specimen> standings,
                                               Specimen winner, Specimen loser) {
        population.add(winner);
        standings.add(loser);
        return standings;
    }

    public void chooseBestSpecimens(ArrayList<Specimen> standings) {
        population.addAll(standings.subList(0, NUMBER_OF_BEST_TO_SELECT));
    }

    public void createChildrenByMutation() {
        int childrenToCreate = POPULATION_SIZE - NUMBER_OF_BEST_TO_SELECT;
        if (NUMBER_OF_BEST_TO_SELECT == 1) {
            Specimen parent = population.get(0);
            for (int i=0; i<childrenToCreate; i++) {
                population.add(createChildByMutation(parent));
            }
        }
        else {
            //what to do if more best are selected?
        }
    }

    public Specimen createChildByCrossing(Specimen firstParent, Specimen secondParent) {
        float[] firstParameters = new float[] {
                firstParent.getQueensWeight(),
                firstParent.getPawnsWeight(),
                firstParent.getPawnsPositionsWeight(),
                firstParent.getPawnsStructuresWeight()
        };
        float[] secondParameters = new float[] {
                secondParent.getQueensWeight(),
                secondParent.getPawnsWeight(),
                secondParent.getPawnsPositionsWeight(),
                secondParent.getPawnsStructuresWeight()
        };
        float[] childParameters = new float[4];

        for (int i=0; i<firstParameters.length; i++) {
            Random random = new Random();
            if (random.nextBoolean()) {
               childParameters[i] = firstParameters[i];
            } else {
                childParameters[i] = secondParameters[i];
            }
        }
        return new Specimen(childParameters[0], childParameters[1], childParameters[2], childParameters[3]);
    }

    public Specimen createChildByMutation(Specimen parent) {
        Random random = new Random();
        float queensParam = parent.getQueensWeight() + mutationFactor * (float) random.nextGaussian();
        float pawnsParam = parent.getPawnsWeight() + mutationFactor * (float) random.nextGaussian();
        float pawnsPosParam = parent.getPawnsPositionsWeight() + mutationFactor * (float) random.nextGaussian();
        float pawnsStructParam = parent.getPawnsStructuresWeight() + mutationFactor * (float) random.nextGaussian();

        return new Specimen(queensParam, pawnsParam, pawnsPosParam, pawnsStructParam);
    }

    public GameEngine.GameState playGame(Specimen white, Specimen black) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
        boolean whiteToMove = true;

        MainAlgorithm mainAlgorithm = new MainAlgorithm(ALGORITHM_DEPTH, gameEngine);
        mainAlgorithm.calculateTree(white);

        while(gameEngine.getGameState() == GameEngine.GameState.RUNNING) {
            Move<? extends Hop> move = mainAlgorithm.findBestMove();

            mainAlgorithm.getMoveTree().moveDown(move);
            mainAlgorithm.getMoveTree().setCurrentNodeAsRoot();

            whiteToMove = !whiteToMove;
            int levelToCalculate = ALGORITHM_DEPTH + mainAlgorithm.getMoveTree().getRoot().getLevel();
            if (whiteToMove) {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate, white);
            } else {
                mainAlgorithm.calculateNextTreeLevel(levelToCalculate, black);
            }
        }

        return gameEngine.getGameState();
    }

}
