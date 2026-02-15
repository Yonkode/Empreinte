package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;
import Enhancement_STFT.Probability.ComputeProbability;
import Enhancement_STFT.Probability.ComputeProbabilityTheta;
import Enhancement_STFT.Windows.ComputeWindowOfSTFT;

import static Enhancement_STFT.FourrierTransform.FourierTransform2D.ComputeFourierTransform2D;

public class Orientation {

    double[][] computeOrientation(int [][] image, int block)
    {
        int heigth = image.length;
        int width = image[0].length;
        int  margin = block + 1;
        double[][] BLKSZ = new double[block][block];
        double[][] theta = new double[block][block];
        double[][] proba = new double[block][block];
        double[][] window = ComputeWindowOfSTFT.computeWindow();
        double[] probatheta = new double[block];
        double energy;
        double[][] ori = new double[heigth][width];

        for (int y = margin; y < heigth - margin; y += block)
        {
            for (int x = margin; x < width - margin; x += block)
            {
                for (int i = 0; i < block ; i++) {
                    for (int j = 0 ; j < block ; j++) {

                        BLKSZ[i][j] = image[y + i][x + j] ;
                        theta[i][j] = Math.atan2(x + j,y + i);
                    }
                }
                Complex [][] transform = new Complex[block][block];
                BLKSZ = ComputeWindowOfSTFT.convolution2D(BLKSZ, window);
                transform = ComputeFourierTransform2D(BLKSZ);
                proba = ComputeProbability.probability(transform);
                probatheta = ComputeProbabilityTheta.probabilityOfTheta(proba);
                energy = computeEnergy(probatheta, theta[0]);

                for (int i = 0; i < block; i++)
                    for (int j = 0; j < block; j++)
                        if (y + i < heigth && x + j < width)
                            ori[y + i][x + j] = energy;
            }
        }

        return ori;
    }

    double computeEnergy(double [] probaTheta, double [] theta)
    {
        double energy = 0;
        int heigth = probaTheta.length;
        double x = 0, y = 0;

        if(heigth != theta.length)
        {
            System.out.println("!!! PROBLEME");
        }
        else
        {
            for (int i=0; i< heigth; i++)
            {
                y +=probaTheta[i]*Math.sin(2*theta[i]);
                x +=probaTheta[i]*Math.cos(2*theta[i]);
            }
            energy= (double) 1 /2 * Math.atan2(y,x);
        }

        return energy;
    }

    double[][] computeSinPart(double[][] orientation)
    {
        int heigth = orientation.length, width = orientation[0].length;
        double[][] sinPart = new double[heigth][width];

        for (int i= 0; i<heigth; i++)
        {
            for (int j= 0; j<width; j++)
            {
                sinPart[i][j] = Math.sin(orientation[i][j]);
            }
        }
        return sinPart;
    }
    double[][] computeCosPart(double[][] orientation)
    {
        int heigth = orientation.length, width = orientation[0].length;
        double[][] cosPart = new double[heigth][width];

        for (int i= 0; i<heigth; i++)
        {
            for (int j= 0; j<width; j++)
            {
                cosPart[i][j] = Math.sin(orientation[i][j]);
            }
        }
        return cosPart;
    }
    double[][] smoothOrientation(double[][] orientation)
    {
        int heigth = orientation.length, width = orientation[0].length;
        double [][] cosPart = computeCosPart(orientation);
        double[][] sinPart = computeSinPart(orientation);
        double[][] orientationSmooth = new double[heigth][width];

        double[][] gaussianKernel = {
                {1/273.0, 4/273.0, 7/273.0, 4/273.0, 1/273.0},
                {4/273.0,16/273.0,26/273.0,16/273.0, 4/273.0},
                {7/273.0,26/273.0,41/273.0,26/273.0, 7/273.0},
                {4/273.0,16/273.0,26/273.0,16/273.0, 4/273.0},
                {1/273.0, 4/273.0, 7/273.0, 4/273.0, 1/273.0}
        };
        double [][] cosPartSmooth = ComputeWindowOfSTFT.convolution2D(cosPart, gaussianKernel);
        double [][] sinPartSmooth = ComputeWindowOfSTFT.convolution2D(sinPart, gaussianKernel);

        for (int i= 0; i<heigth; i++)
        {
            for (int j= 0; j<width; j++)
            {
                orientationSmooth[i][j] = 1.0/2.0 *Math.atan2(sinPartSmooth[i][j],cosPartSmooth[i][j]);
            }
        }
        return orientationSmooth;
    }
}
