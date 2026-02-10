package Enhancement;

public class Orientation {
    static double[][] computeOrientation(int[][] img, int block) {
        int h = img.length;
        int w = img[0].length;
        double[][] ori = new double[h][w];

        int margin = block + 1; // marge de sécurité

        for (int y = margin; y < h - margin; y += block) {
            for (int x = margin; x < w - margin; x += block) {

                double gx = 0, gy = 0;

                for (int i = -block / 2; i <= block / 2; i++) {
                    for (int j = -block / 2; j <= block / 2; j++) {

                        int dx = img[y + i][x + j + 1] - img[y + i][x + j - 1];
                        int dy = img[y + i + 1][x + j] - img[y + i - 1][x + j];

                        gx += 2 * dx * dy;
                        gy += dx * dx - dy * dy;
                    }
                }

                double theta = 0.5 * Math.atan2(gx, gy);

                for (int i = 0; i < block; i++)
                    for (int j = 0; j < block; j++)
                        if (y + i < h && x + j < w)
                            ori[y + i][x + j] = theta;
            }
        }
        return ori;
    }
}
