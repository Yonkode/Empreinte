package Enhancement_STFT.FourrierTransform;

public class InverseFourierTransform {
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
                        Complex eulerExpression,inter;
                        eulerExpression =new Complex(Math.cos(2*Math.PI*(((double)i * u)/height + ((double)j * v)/width)), Math.sin(2*Math.PI*(((double)i * u)/height + ((double)j * v)/width)));
                        inter = eulerExpression.multiply(fourierTransform[i][j]);
                        sumReal += inter.getReal();
                        sumImg +=  inter.getImg() ;
                    }
                }
                out[u][v] = sumReal / (height * width);
            }
        }
        return out;
    }
}
