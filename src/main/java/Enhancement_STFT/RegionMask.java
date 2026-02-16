package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;

public class RegionMask {
    public int[][] computeMask(double[][] energy, double threshold)
    {
        int height = energy.length;
        int width = energy[0].length;
        int[][] mask = new int[height][width];

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                mask[i][j] = (energy[i][j] >= threshold) ? 1 : 0;
            }
        }
        return mask;
    }

    public double[][] computeBlockEnergy(Complex[][] transform)
    {
        int heigth = transform.length;
        int width = transform[0].length;
        double[][] energy = new double[heigth][width];

        for (int i =0; i<heigth; i++)
        {
            for(int j = 0; j<width; j++)
            {
                energy[i][j] = Math.log(Math.pow(transform[i][j].magnitude(),2));
            }
        }
        return energy;
    }
}
