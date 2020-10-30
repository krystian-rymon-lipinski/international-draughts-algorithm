package com.krystianrymonlipinski.algorithm.geneticalgorithm;

import java.util.Random;

public class Specimen {

    private float queensParameter;
    private float pawnsParameter;
    private float pawnsPositionParameter;

    public Specimen(int range) {
        this.generateRandomSpecimen(range);
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

    public void generateRandomSpecimen(int range) {
        Random random = new Random();
        this.queensParameter = range * 2 * (random.nextFloat() - 0.5f); //allow negative values of a parameter
        this.pawnsParameter = range * 2 * (random.nextFloat() - 0.5f);
        this.pawnsPositionParameter = range * 2 * (random.nextFloat() - 0.5f);
    }
}
