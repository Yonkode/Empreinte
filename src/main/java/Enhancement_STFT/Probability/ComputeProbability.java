package Enhancement_STFT.Probability;

import Enhancement_STFT.FourrierTransform.Complex;

public class ComputeProbability {
    public static double[][] probability(Complex[][] fourier)
    {
        int height = fourier.length;
        int width = fourier[0].length;
        double p = 0;
        double [][] proba = new double[height][width];
        for (Complex[] complexes : fourier) {
            for (int j = 0; j < width; j++) {
                p += Math.pow(complexes[j].magnitude(), 2);
            }
        }

        for(int i = 0; i< height; i++)
        {
            for (int j =0; j<width; j++)
            {
                proba[i][j] = Math.pow(fourier[i][j].magnitude(), 2)/p;
            }
        }
        return proba;
    }
}
