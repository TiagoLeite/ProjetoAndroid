package br.usp.trabalhoandroid;

import android.net.Uri;

import java.io.Serializable;

public class ExerciseVideo implements Serializable
{
    private Uri uri;
    private String description;

    public ExerciseVideo(String description, Uri uri)
    {
        this.description = description;
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public Uri getUri() {
        return uri;
    }
}
