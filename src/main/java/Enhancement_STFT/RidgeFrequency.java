package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;

public class RidgeFrequency {
    public double[][] computeFrequency(Complex[][] imgFFT, double mask)
    {
        int heigth = imgFFT.length, width = imgFFT[0].length;
        double[][] frequency = new double[heigth][width];
        double[][] gaussian3x3 = {
                {0.0625, 0.1250, 0.0625},
                {0.1250, 0.2500, 0.1250},
                {0.0625, 0.1250, 0.0625}
        };
        int heigthG =gaussian3x3.length;

        for (int u = 0; u <heigth ; u++)
        {
            for (int v = 0; v <heigth ; v++)
            {
                for (int i = -heigthG/2; i<heigthG/2 ; i++)
                {
                    for (int j = heigthG/2; j<heigthG/2 ; j++)
                    {
                        
                    }
                }
            }
        }
    }
}
