package com.applicationslab.ayurvedictreatment.datamodel;

import java.util.ArrayList;


public class DiseaseSearchData {

    private String diseaseName;
    private ArrayList<Integer>plantPositions;

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public ArrayList<Integer> getPlantPositions() {
        return plantPositions;
    }

    public void setPlantPositions(ArrayList<Integer> plantPositions) {
        this.plantPositions = plantPositions;
    }

}
