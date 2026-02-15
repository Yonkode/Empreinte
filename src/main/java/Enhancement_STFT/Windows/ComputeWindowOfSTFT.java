package Enhancement_STFT.Windows;

public class ComputeWindowOfSTFT {
    public static double[][] computeWindow() {

        int WNDSZ = 12;
        int OVRLP = 6; // 50% overlap

        double[][] window = new double[WNDSZ][WNDSZ];

        int half = WNDSZ / 2;

        for (int i = -half; i < half; i++) {
            for (int j = -half; j < half; j++) {

                double wx = 1.0;
                double wy = 1.0;

                if (Math.abs(i) >= half - OVRLP) {
                    wx = 0.5 * (1 + Math.cos(
                            Math.PI * (Math.abs(i) - (half - OVRLP)) / OVRLP
                    ));
                }

                if (Math.abs(j) >= half - OVRLP) {
                    wy = 0.5 * (1 + Math.cos(
                            Math.PI * (Math.abs(j) - (half - OVRLP)) / OVRLP
                    ));
                }

                window[i + half][j + half] = wx * wy;
            }
        }

        return window;
    }


    public static double[][] convolution2D(double [][] image, double[][] filter) {

        int height = image.length;
        int width = image[0].length;

        int fHeight = filter.length;
        int fWidth = filter[0].length;

        int hHalf = fHeight / 2;
        int wHalf = fWidth / 2;

        double[][] result = new double[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                double sum = 0.0;

                for (int m = 0; m < fHeight; m++) {
                    for (int n = 0; n < fWidth; n++) {

                        int ii = i + m - hHalf;
                        int jj = j + n - wHalf;

                        if (ii >= 0 && ii < height &&
                                jj >= 0 && jj < width) {

                            sum += image[ii][jj] * filter[m][n];
                        }
                    }
                }

                result[i][j] = sum;
            }
        }

        return result;
    }
}
