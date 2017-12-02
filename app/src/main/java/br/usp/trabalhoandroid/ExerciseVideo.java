package br.usp.trabalhoandroid;

import java.io.Serializable;

public class ExerciseVideo implements Serializable
{
    private String uriString;
    private String description;

    public ExerciseVideo(String description, String uri)
    {
        this.description = description;
        this.uriString = uri;
    }

    public String getDescription() {
        return description;
    }

    public String getUriString() {
        return uriString;
    }
}
