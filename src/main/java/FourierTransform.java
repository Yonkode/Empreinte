public class FourierTransform {
   static class Complex {
    double re, im;
    Complex(double r, double i) {
        re = r; im = i;
    }
}
    public static Complex[] dft1D(double[] signal) {
        int N = signal.length;
        Complex[] out = new Complex[N];
        double omega = 2.0 * Math.PI / N;

        for (int k = 0; k < N; k++) {
            double sumRe = 0;
            double sumIm = 0;

            for (int n = 0; n < N; n++) {
                double angle = omega * k * n;
                sumRe += signal[n] * Math.cos(angle);
                sumIm -= signal[n] * Math.sin(angle); // SIGNE IMPORTANT
            }
            out[k] = new Complex(sumRe, sumIm);
        }
        return out;
    }

    public static double[] applyHanning(double[] signal) {
        int N = signal.length;
        double[] out = new double[N];

        for (int n = 0; n < N; n++) {
            double w = 0.5 - 0.5 * Math.cos(2 * Math.PI * n / (N - 1));
            out[n] = signal[n] * w;
        }
        return out;
    }

    public static float [] FourierTransform1D(int [] img){
        int lengthImg = img.length;
        float omega =(float) (2 * Math.PI)/lengthImg;
        float [] FT1D = new float[lengthImg];
        
        for(int i = 0; i < lengthImg; i++){
            float sumRe=0;
            float sumImg=0;
            for(int j= 0; j < lengthImg; j++){
                sumRe += img[j]*Math.cos(omega*i*j);
                sumImg += img[j]*Math.sin(omega*i*j);
            }
            FT1D[i] =(float) Math.sqrt(sumRe*sumRe + sumImg * sumImg);
        }

        return FT1D;
    }

    public static double [][] FourierTransform2D(int [][] img){
        int getHeigth = img.length;
        int getWidth = img[0].length;
        double [][] FT2D = new double [getHeigth][getWidth];

        for(int x = 0; x<getHeigth; x++){
            for(int y = 0; y<getWidth; y++){
                double r= Math.sqrt(x*x/getHeigth*getHeigth + y*y/getWidth*getWidth);
                double theta = Math.atan((double)y/getWidth, (double)x/getHeigth);
                for(int i= 0; i<getHeigth; i++){
                    for(int j = 0; j<getWidth; j++){
                        FT2D[x][y] = Math.sqrt(Math.pow(img[i][j] 
                                    * Math.cos(2
                                        *Math.PI*(r*(i
                                            *Math.cos(theta) +j
                                            *Math.sin(theta)))),2) 
                                        + Math.pow(img[i][j] 
                                            * Math.sin(2
                                            *Math.PI*(r*(i*Math.cos(theta) +j
                                            *Math.sin(theta)))),2));
                    }
                }
            }
        }
        return FT2D;
    }
}
