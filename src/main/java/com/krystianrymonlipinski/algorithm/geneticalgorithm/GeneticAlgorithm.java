package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import com.krystianrymonlipinski.algorithm.playingalgorithm.MainAlgorithm;
import draughts.library.managers.GameEngine;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

    private final static int ALGORITHM_DEPTH = 4;

    private final static int NUMBER_OF_GENERATIONS = 20;
    private final static int POPULATION_SIZE = 6;
    private final static int NUMBER_OF_BEST_TO_SELECT = 1;

    private final static int PARAMETER_RANGE_MIN = -1;
    private final static int PARAMETER_RANGE_MAX = 10;
    private final static float MUTATION_FACTOR = 0.2f;


    //private int crossingRange =

    private ArrayList<Specimen> specimens = new ArrayList<>();




    public ArrayList<Specimen> getSpecimens() {
        return specimens;
    }

    public void trainAlgorithm() {
        createFirstGeneration();

        for (int i=0; i<NUMBER_OF_GENERATIONS; i++) {
            ArrayList<TournamentPlayer> standings = playTournament();
            chooseBestSpecimens(standings);
            createChildrenByCrossing();
            mutate();
        }
    }

    public void createFirstGeneration() {
        for (int i = 0; i< POPULATION_SIZE; i++) {
            specimens.add(new Specimen(PARAMETER_RANGE_MIN, PARAMETER_RANGE_MAX));
            System.out.println(specimens.get(i));
        }
    }

    public ArrayList<TournamentPlayer> playTournament() { //every algorithm with every algorithm
        ArrayList<TournamentPlayer> tournamentStandings = new ArrayList<>();
        for (int i = 0; i< POPULATION_SIZE; i++) {
            tournamentStandings.add(new TournamentPlayer(specimens.get(i)));
        }

        for (int i=0; i<tournamentStandings.size(); i++) {
            for (int j=0; j<tournamentStandings.size(); j++) {
                if (i > j) {
                    boolean changeColors = new Random().nextBoolean();
                    GameEngine.GameState gameResult;
                    if (changeColors) {
                        gameResult = playGame(tournamentStandings.get(j).getPlayer(), tournamentStandings.get(j).getPlayer());
                    } else {
                        gameResult = playGame(tournamentStandings.get(i).getPlayer(), tournamentStandings.get(j).getPlayer());
                    }

                    switch (gameResult) {
                        case WON_BY_WHITE:
                                if (changeColors) tournamentStandings.get(j).addWin();
                                else              tournamentStandings.get(i).addWin();
                            break;
                        case WON_BY_BLACK:
                                if (changeColors) tournamentStandings.get(i).addWin();
                                else              tournamentStandings.get(j).addWin();
                            break;
                        case DRAWN:
                            tournamentStandings.get(i).addDraw();
                            tournamentStandings.get(j).addDraw();
                    }
                }
            }
        }

        Collections.sort(tournamentStandings);
        return tournamentStandings;
    }

    public void chooseBestSpecimens(ArrayList<TournamentPlayer> tournamentStandings) {
        specimens.clear();

        for (int i=0; i<NUMBER_OF_BEST_TO_SELECT; i++) {
            specimens.add(tournamentStandings.get(i).player);
        }
    }

    public void createChildrenByCrossing() {

    }

    public void mutate() {

    }

    public GameEngine.GameState playGame(Specimen white, Specimen black) {
        System.out.println("First: " + white + " Second: " + black);
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

    private static class TournamentPlayer implements Comparable<TournamentPlayer>{
        private Specimen player;
        private float score;

        private TournamentPlayer(Specimen specimen) {
            this.player = specimen;
            this.score = 0;
        }

        public Specimen getPlayer() {
            return player;
        }

        public float getScore() {
            return score;
        }

        public void addWin() {
            this.score++;
        }

        public void addDraw() {
            this.score += 0.5f;
        }

        @Override
        public int compareTo(TournamentPlayer player) {
            if (this.score - player.score < 0)      return 1;
            else if (this.score - player.score > 0) return -1;
            else                                    return 0;
        }
    }

}
