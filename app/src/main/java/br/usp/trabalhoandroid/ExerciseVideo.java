package br.usp.trabalhoandroid;

import java.io.Serializable;

public class ExerciseVideo implements Serializable
{
    String description;

    public ExerciseVideo(String description)
    {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
