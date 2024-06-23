package com.example.androidchatrobot.pojo;

public class Setting {
    private Double Temperature;

    private int maxContextNumber;

    private String ApiToken;

    public Setting(Double temperature, int maxContextNumber, String apiToken, int modelType,int detailType) {
        Temperature = temperature;
        this.maxContextNumber = maxContextNumber;
        ApiToken = apiToken;
        this.modelType = modelType;
        this.detailType=detailType;
    }

    private int modelType;

    private String modelName;

    private int detailType;

    private String detailName;

    private boolean UseLatex;

    public int getDetailType() {
        return detailType;
    }

    public void setDetailType(int detailType) {
        this.detailType = detailType;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }



    public Double getTemperature() {
        return Temperature;
    }

    public void setTemperature(Double temperature) {
        Temperature = temperature;
    }

    public int getMaxContextNumber() {
        return maxContextNumber;
    }

    public void setMaxContextNumber(int maxContextNumber) {
        this.maxContextNumber = maxContextNumber;
    }

    public String getApiToken() {
        return ApiToken;
    }

    public void setApiToken(String apiToken) {
        ApiToken = apiToken;
    }

    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }


}
