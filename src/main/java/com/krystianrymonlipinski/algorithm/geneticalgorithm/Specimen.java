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
        this.queensParameter = range * random.nextFloat();
        this.pawnsParameter = range * random.nextFloat();
        this.pawnsPositionParameter = range * random.nextFloat();
    }

    @Override
    public String toString() {
        return "Specimen{" +
                "queensParameter = " + queensParameter +
                ", pawnsParameter = " + pawnsParameter +
                ", pawnsPositionParameter = " + pawnsPositionParameter +
                '}';
    }
}
