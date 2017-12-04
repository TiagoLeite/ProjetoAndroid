package br.usp.trabalhoandroid;

import android.util.Log;

import java.io.Serializable;

public class Exercise implements Serializable
{
    private String videoUriString;
    private String name;
    private double series[][];
    private int sizeSeries;

    public Exercise()
    {
        this.series = new double[3][2048]; // x, y, z, with 2048 values each one
        this.sizeSeries = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVideoUriString(String videoUriString) {
        this.videoUriString = videoUriString;
    }

    public void updateSeries(double x, double y, double z)
    {
        series[0][sizeSeries] = x;
        series[1][sizeSeries] = y;
        series[2][sizeSeries++] = z;
    }

    public int getSizeSeries() {
        return sizeSeries;
    }

    public String getName() {
        return name;
    }

    public double[][] getSeries() {
        return series;
    }

    public String getVideoUriString() {
        return videoUriString;
    }

    public static double DTW(double[] seriesA, int sizeA, double[] seriesB, int sizeB, double faixa)
    {
        int i, j;
        double dist;
        double dtw[][] = new double[sizeA+1][sizeB+1];

        for(i=1; i <= sizeA; i++)
            dtw[i][0] = Double.MAX_VALUE;

        for(i=1; i <= sizeB; i++)
            dtw[0][i] = Double.MAX_VALUE;

        for(i=1; i <= sizeA; i++)//banda Sakoe Chiba
        {
            for (j = 1; j <= sizeB; j++)
            {
                if (Math.abs((i - 1) - (int) ((j - 1) * (((double) (sizeA - 1) / (double) (sizeB - 1)))))
                        <= (int) (Math.ceil((faixa) * ((double) (sizeA - 1)))))
                {
                    dtw[i][j] = 0;
                }
                else
                {
                    dtw[i][j] = Double.MAX_VALUE;
                }
            }
            System.out.println();
        }

        dtw[0][0] = 0;

        for(i=1; i <= sizeA; i++)
        {
            for(j=1; j <= sizeB; j++)
            {
                if(dtw[i][j]==0)
                {
                    dist = Math.pow((seriesA[i-1] - seriesB[j-1]), 2);
                    dtw[i][j] = dist + minimum(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]);
                }
            }
        }

        return dtw[sizeA][sizeB];
    }

    private static double minimum(double a, double b, double c)
    {
        double min;
        min = (a < b) ? a : b;
        min = (min < c) ? min : c;
        return min;
    }

    public void printSeries()
    {
        Log.d("debug", "size:" + sizeSeries);
        for(int k = 0; k < sizeSeries; k++)
            Log.d("debug", series[0][k] + " " + series[1][k] + " " + series[2][k]);
    }

    public double calcDistanceOfSeries(Exercise otherExercise)
    {
        double[][] seriesDoctor = otherExercise.getSeries();
        double[][] seriesUser = this.series;
        int sizeSeriesB = this.sizeSeries;
        int sizeSeriesA = otherExercise.sizeSeries;
        double distance = 0f;
        distance += Exercise.DTW(seriesDoctor[0], sizeSeriesA,
                seriesUser[0], sizeSeriesB,
                .05);
        distance += Exercise.DTW(seriesDoctor[1], sizeSeriesA,
                seriesUser[1], sizeSeriesB,
                0.05);
        distance += Exercise.DTW(seriesDoctor[2], sizeSeriesA,
                seriesUser[2], sizeSeriesB,
                0.05);
        return distance;
        //((TextView)view.findViewById(R.id.tv_distance)).setText(String.format("%.2f", (1000f - distance) / 10f) + "%");
    }

}
