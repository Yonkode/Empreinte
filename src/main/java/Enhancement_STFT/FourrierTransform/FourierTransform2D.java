package Enhancement_STFT.FourrierTransform;

public class FourierTransform2D{

    public static Complex[] fft(Complex[] x) {

        int N = x.length;

        if (N == 1)
            return new Complex[]{x[0]};

        if (N % 2 != 0)
            throw new IllegalArgumentException("Length is not power of 2");

        // Separate even / odd
        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];

        for (int i = 0; i < N / 2; i++) {
            even[i] = x[2 * i];
            odd[i] = x[2 * i + 1];
        }

        Complex[] Feven = fft(even);
        Complex[] Fodd = fft(odd);

        Complex[] F = new Complex[N];

        for (int k = 0; k < N / 2; k++) {

            double angle = -2 * Math.PI * k / N;
            Complex wk = new Complex(Math.cos(angle), Math.sin(angle));

            Complex t = wk.multiply(Fodd[k]);

            F[k] = Feven[k].add(t);
            F[k + N / 2] = Feven[k].subtract(t);
        }

        return F;
    }



        public static Complex[][] fft2D(double [][] signal2D) {

            int height = signal2D.length;
            int width = signal2D[0].length;

            Complex[][] data = new Complex[height][width];

            // Convert to Complex
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    data[i][j] = new Complex(signal2D[i][j]);
                }
            }

            // FFT on rows
            for (int i = 0; i < height; i++) {
                data[i] = fft(data[i]);
            }

            // FFT on columns
            for (int j = 0; j < width; j++) {

                Complex[] column = new Complex[height];

                for (int i = 0; i < height; i++)
                    column[i] = data[i][j];

                column = fft(column);

                for (int i = 0; i < height; i++)
                    data[i][j] = column[i];
            }

            return data;
        }

        public static Complex [][] ComputeFourierTransform2D(double[][] imgW)
        {
            int height = imgW.length;
            int width = imgW[0].length;
            Complex [][] out = new Complex[height][width];

            for (int u = 0 ; u < height; u++)
            {
                for (int v = 0 ; v < width; v++)
                {
                    double img = 0;
                    double real = 0;
                    for (int i = 0 ; i < height; i++)
                    {
                        for (int j = 0 ; j < width; j++)
                        {
                            double a = 2 * Math.PI * ((double) (u * i) / height + (double) (v * j) / width);
                            real += imgW[i][j] * Math.cos(a) ;
                            img -= imgW[i][j] * Math.sin(a) ;
                        }
                    }
                    out[u][v] = new Complex(real,img);
                }
            }
            return out;
        }

}