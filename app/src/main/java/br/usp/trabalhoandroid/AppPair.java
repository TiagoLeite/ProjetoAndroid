package br.usp.trabalhoandroid;

import java.io.Serializable;

public class AppPair<E, T>  implements Serializable
{
    public E first;
    public T second;

    public AppPair(E first, T second)
    {
        this.first = first;
        this.second = second;
    }
}
