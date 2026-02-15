package Enhancement_STFT;

public class Normalization {
    public  static int[][] normalize(int[][] img, int targetMean, int targetVar) {
        int h = img.length, w = img[0].length;
        double mean = 0, var = 0;

        for (int[] row : img)
            for (int v : row) mean += v;
        mean /= (h * w);

        for (int[] row : img)
            for (int v : row) var += Math.pow(v - mean, 2);
        var /= (h * w);

        int[][] out = new int[h][w];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                double val = img[y][x];
                double norm = Math.sqrt(targetVar * Math.pow(val - mean, 2) / var);
                out[y][x] = (int) (val > mean ? targetMean + norm : targetMean - norm);
            }
        return out;
    }

    public double[][] remorveDCValue(double [][] block)
    {
        int heigth = block.length, width = block[0].length;
        double [][] out = new double[heigth][width];
        double sum =0;

        for (double[] doubles : block) {
            for (int j = 0; j < width; j++) {
                sum += doubles[j];
            }
        }
        sum = sum /(heigth * width);

        for (int i = 0 ; i<heigth; i++)
        {
            for (int j = 0 ; j<width; j++)
            {
                out[i][j] = block[i][j] - sum;
            }
        }
        return out;
    }
}
