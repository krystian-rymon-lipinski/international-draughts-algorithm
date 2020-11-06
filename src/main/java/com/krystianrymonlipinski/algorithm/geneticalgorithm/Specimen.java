package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import java.util.Random;

public class Specimen {

    private float queensParameter;
    private float pawnsParameter;
    private float pawnsPositionParameter;

    public Specimen(int rangeMin, int rangeMax) {
        this.generateRandomSpecimen(rangeMin, rangeMax);
    }

    public Specimen(float queensParameter, float pawnsParameter, float pawnsPositionParameter) {
        this.queensParameter = queensParameter;
        this.pawnsParameter = pawnsParameter;
        this.pawnsPositionParameter = pawnsPositionParameter;
    }

    public float getQueensParameter() {
        return queensParameter;
    }

    public void setQueensParameter(float queensParameter) {
        this.queensParameter = queensParameter;
    }

    public float getPawnsParameter() {
        return pawnsParameter;
    }

    public void setPawnsParameter(float pawnsParameter) {
        this.pawnsParameter = pawnsParameter;
    }

    public float getPawnsPositionParameter() {
        return pawnsPositionParameter;
    }

    public void setPawnsPositionParameter(float pawnsPositionParameter) {
        this.pawnsPositionParameter = pawnsPositionParameter;
    }

    public void generateRandomSpecimen(int rangeMin, int rangeMax) {
        Random random = new Random();
        int range = rangeMin + rangeMax;
        this.queensParameter = range * random.nextFloat() - Math.abs(rangeMin);
        this.pawnsParameter = range * random.nextFloat() - Math.abs(rangeMin);
        this.pawnsPositionParameter = range * random.nextFloat() - Math.abs(rangeMin);
    }

    @Override
    public String toString() {
        return "Specimen{" +
                "a = " + queensParameter +
                ", b = " + pawnsParameter +
                ", c = " + pawnsPositionParameter +
                '}';
    }
}
