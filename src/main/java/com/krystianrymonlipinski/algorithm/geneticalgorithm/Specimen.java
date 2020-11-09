package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import java.util.Random;

public class Specimen {

    private float queensWeight;
    private float pawnsWeight;
    private float pawnsPositionsWeight;
    private float pawnsStructuresWeight;

    public Specimen(int rangeMin, int rangeMax) {
        this.generateRandomSpecimen(rangeMin, rangeMax);
    }

    public Specimen(float queensWeight, float pawnsWeight, float pawnsPositionsWeight, float pawnsStructuresWeight) {
        this.queensWeight = queensWeight;
        this.pawnsWeight = pawnsWeight;
        this.pawnsPositionsWeight = pawnsPositionsWeight;
        this.pawnsStructuresWeight = pawnsStructuresWeight;
    }

    public float getQueensWeight() {
        return queensWeight;
    }

    public void setQueensWeight(float queensWeight) {
        this.queensWeight = queensWeight;
    }

    public float getPawnsWeight() {
        return pawnsWeight;
    }

    public void setPawnsWeight(float pawnsWeight) {
        this.pawnsWeight = pawnsWeight;
    }

    public float getPawnsPositionsWeight() {
        return pawnsPositionsWeight;
    }

    public void setPawnsPositionsWeight(float pawnsPositionsWeight) {
        this.pawnsPositionsWeight = pawnsPositionsWeight;
    }

    public float getPawnsStructuresWeight() {
        return pawnsStructuresWeight;
    }

    public void setPawnsStructuresWeight(float pawnsStructuresWeight) {
        this.pawnsStructuresWeight = pawnsStructuresWeight;
    }

    public void generateRandomSpecimen(int rangeMin, int rangeMax) {
        Random random = new Random();
        int range = rangeMin + rangeMax;
        this.queensWeight = range * random.nextFloat() - Math.abs(rangeMin);
        this.pawnsWeight = range * random.nextFloat() - Math.abs(rangeMin);
        this.pawnsPositionsWeight = range * random.nextFloat() - Math.abs(rangeMin);
        this.pawnsStructuresWeight = range * random.nextFloat() - Math.abs(rangeMin);
    }

    @Override
    public String toString() {
        return "Specimen{" +
                "a = " + queensWeight +
                ", b = " + pawnsWeight +
                ", c = " + pawnsPositionsWeight +
                ", d = " + pawnsStructuresWeight +
                '}';
    }
}
