package br.usp.trabalhoandroid;

import java.io.Serializable;

public class AppPair<E, T>  implements Serializable
{
    private static int id_count = 0;
    private int id;
    public E first;
    public T second;

    public AppPair(E first, T second)
    {
        this.first = first;
        this.second = second;
        this.id = id_count++;
    }

    public int getId() {
        return id;
    }
}
