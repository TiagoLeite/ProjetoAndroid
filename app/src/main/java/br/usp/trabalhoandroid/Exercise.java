package br.usp.trabalhoandroid;


public class Exercise
{
    private String name;

    public Exercise(String name)
    {
        this.name = name;
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

}
