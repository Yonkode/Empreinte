package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;
import Enhancement_STFT.Probability.ComputeProbability;
import Enhancement_STFT.Probability.ComputeProbabilityTheta;
import Enhancement_STFT.Windows.ComputeWindowOfSTFT;

import static Enhancement_STFT.FourrierTransform.FourierTransform2D.ComputeFourierTransform2D;

public class Orientation {

    public double[][] computeOrientation(int [][] image, int block)
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

    public double computeEnergy(double [] probaTheta, double [] theta)
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
            energy= (double) 0.5 * Math.atan2(y,x);
        }

        return energy;
    }

    public double[][] computeSinPart(double[][] orientation)
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

    public double[][] computeCosPart(double[][] orientation)
    {
        int heigth = orientation.length, width = orientation[0].length;
        double[][] cosPart = new double[heigth][width];

        for (int i= 0; i<heigth; i++)
        {
            for (int j= 0; j<width; j++)
            {
                cosPart[i][j] = Math.cos(orientation[i][j]);
            }
        }
        return cosPart;
    }

    public double[][] smoothOrientation(double[][] orientation)
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

    public double[][] computeCoherence(double [][] orientation, int block, int W)
    {
        int heigth = orientation.length;
        int width = orientation[0].length;

        double[] similarOrientation = new double[W*W];
        double[][] coherence = new double[heigth][width];

        for (int y = 0; y < heigth ; y += block)
        {
            for (int x = 0; x < width ; x += block)
            {
                int val1,val2;
                double sum = 0;
                int count = 0;
                for (int i = -W/2; i <= W/2; i++){
                    for (int j = -W/2; j <= W/2; j++){
                        val1 = y + i*block;
                        val2 = x + j*block;
                        if(val1< 0 || val2< 0 || val1 >= heigth || val2 >= width)
                        {
                            sum += 0;
                        }
                        else
                        {
                            sum += Math.abs(Math.cos(2*(orientation[y][x] - orientation[val1][val2])));
                            count++;
                        }
                    }
                }
                sum = sum/count;

                for (int i = 0; i < block; i++)
                {
                    for (int j = 0; j < block; j++)
                    {
                        int yy = y + i;
                        int xx = x + j;
                        if (yy < heigth && xx < width)
                            coherence[yy][xx] = sum;
                    }
                }
            }
        }
        return coherence;
    }
}
