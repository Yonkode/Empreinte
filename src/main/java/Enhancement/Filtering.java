package Enhancement;

public class Filtering {
    static int[][] gaborEnhance(
            int[][] img,
            double[][] ori,
            double[][] freq) {

        int h = img.length;
        int w = img[0].length;
        int[][] out = new int[h][w];

        int size = 11;
        int half = size / 2;

        for (int y = half; y < h - half; y++) {
            for (int x = half; x < w - half; x++) {

                double f = freq[y][x];
                if (f <= 0) {
                    out[y][x] = img[y][x];
                    continue;
                }

                double theta = ori[y][x];
                double sum = 0;

                for (int i = -half; i <= half; i++) {
                    for (int j = -half; j <= half; j++) {

                        double xr = j * Math.cos(theta) + i * Math.sin(theta);
                        double yr = -j * Math.sin(theta) + i * Math.cos(theta);

                        double sigma = 1.0 / f;
                        double g = Math.exp(
                                -(xr*xr + yr*yr) / (2 * sigma * sigma))
                                * Math.cos(2 * Math.PI * f * xr);

                        sum += img[y + i][x + j] * g;
                    }
                }

                int val = (int) Math.round(sum);
                out[y][x] = Math.max(0, Math.min(255, val));
            }
        }

        return out;
    }
}
