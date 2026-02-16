package Enhancement_STFT.FourrierTransform;

public class InverseFourierTransform2D {
    public static double []ComputeInverseFourierTransform1D(Complex[] fourierTransform)
    {
        int size = fourierTransform.length;
        double [] out = new double[size];

            for (int w = 0 ; w < size; w++)
            {
                double sumImg = 0;
                double sumReal = 0;
                    for (int x = 0 ; x < size; x++)
                    {
                        double a = 2 * Math.PI * ((double) (w * x) / size );
                        sumReal += fourierTransform[x].getReal() * Math.cos(a) ;
                        sumImg +=  Math.abs(fourierTransform[x].getImg()) * Math.sin(a) ;
                    }
                out[w] = (sumReal + sumImg) / size;
            }
        return out;
    }

    public static double [][] ComputeInverseFourierTransform2D(Complex[][] fourierTransform)
    {
        int height = fourierTransform.length;
        int width = fourierTransform[0].length;
        double [][] out = new double[height][width];

        for (int u = 0 ; u < height; u++)
        {
            for (int v = 0 ; v < width; v++)
            {
                double sumImg = 0;
                double sumReal = 0;
                for (int i = 0 ; i < height; i++)
                {
                    for (int j = 0 ; j < width; j++)
                    {
                        Complex wn,wm,inter;
                        wn =new Complex(Math.cos(2*Math.PI*i*u/height), Math.sin(2*Math.PI*i*u/height));
                        wm = new Complex(Math.cos(2*Math.PI*j*v/width), Math.sin(2*Math.PI*j*v/width));
                        inter = wn.multiply(wm).multiply(fourierTransform[i][j]);
                        sumReal += inter.getReal();
                        sumImg +=  inter.getImg() ;
                    }
                }
                out[u][v] = (sumReal + sumImg) / (height * width);
            }
        }
        return out;
    }
}
