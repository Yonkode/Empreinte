package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;

import static Enhancement_STFT.FourrierTransform.FourierTransform2D.ComputeFourierTransform2D;

public class RidgeFrequency {

    public double[][] computeFrequency(double[][] image, double[][] mask, int block) {
        int height = image.length;
        int width = image[0].length;
        double[][] frequencyMap = new double[height][width];

        // Parcours bloc par bloc
        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {

                // Déterminer la taille du bloc (bord de l'image)
                int blockHeight = Math.min(block, height - y);
                int blockWidth  = Math.min(block, width - x);

                // Extraire le bloc de l'image
                double[][] imgBlock = new double[blockHeight][blockWidth];
                double[][] maskBlock = new double[blockHeight][blockWidth];
                for (int i = 0; i < blockHeight; i++) {
                    for (int j = 0; j < blockWidth; j++) {
                        imgBlock[i][j] = image[y + i][x + j];
                        maskBlock[i][j] = mask[y + i][x + j];
                    }
                }

                // Appliquer une fenêtre sur le bloc si nécessaire (optionnel)
                // imgBlock = ComputeWindowOfSTFT.computeWindowedBlock(imgBlock);

                // FFT du bloc
                Complex[][] fftBlock = ComputeFourierTransform2D(imgBlock);

                // Calcul de la fréquence lissée pour le bloc
                double[][] freqBlock = computeFrequencyForBlock(fftBlock, maskBlock);

                // Remplir la carte globale
                for (int i = 0; i < blockHeight; i++) {
                    for (int j = 0; j < blockWidth; j++) {
                        frequencyMap[y + i][x + j] = freqBlock[i][j];
                    }
                }
            }
        }

        return frequencyMap;
    }

    public double[][] computeFrequencyForBlock(Complex[][] imgFFT, double[][] mask)
    {
        int heigth = imgFFT.length, width = imgFFT[0].length;
        double[][] frequency = new double[heigth][width];
        double[][] filter = {
                {0.0625, 0.1250, 0.0625},
                {0.1250, 0.2500, 0.1250},
                {0.0625, 0.1250, 0.0625}
        };
        int heigthF =filter.length, widthF = filter[0].length;

        for (int u = 0; u <heigth ; u++)
        {
            for (int v = 0; v <width ; v++)
            {
                double sum = 0;
                double val = 0;
                for (int i = -heigthF/2; i<=heigthF/2 ; i++)
                {
                    for (int j = -widthF/2; j<=widthF/2 ; j++)
                    {
                        int ui = u+i;
                        int vj = v+j;

                        if(ui >= 0 && vj >= 0 && ui < heigth && vj < width)
                        {
                            sum += imgFFT[ui][vj].magnitude()*mask[ui][vj]*filter[i +heigthF/2][j+widthF/2];
                            val += mask[ui][vj]*filter[i +heigthF/2][j+widthF/2];
                        }
                    }
                }
                if(val!=0)
                {
                    frequency[u][v] = sum /val;
                }
                else
                {
                    frequency[u][v] = 0;
                }
            }
        }
        return frequency;
    }
}
