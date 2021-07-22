package com.shahdivya.ragaidentification.models;

public class RagaPredicted
{
    private String error;
    private String message;
    private String prediction;

    public RagaPredicted() { }

    public RagaPredicted(String error, String message, String prediction) {
        this.error = error;
        this.message = message;
        this.prediction = prediction;
    }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getPrediction() { return prediction; }

    public void setPrediction(String prediction) { this.prediction = prediction; }
}
