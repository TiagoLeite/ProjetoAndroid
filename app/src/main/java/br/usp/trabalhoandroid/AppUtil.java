package br.usp.trabalhoandroid;


public class AppUtil
{
    public static String formatTime(int hour, int minutes) {
        return ((hour < 10) ? "0" : "") + hour + ":" + ((minutes < 10) ? "0" : "") + minutes;
    }
}
