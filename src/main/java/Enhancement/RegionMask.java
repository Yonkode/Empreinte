package Enhancement;

public class RegionMask {
    static boolean[][] computeROIMask(int[][] img, int block, double varThreshold) {

        int h = img.length, w = img[0].length;
        boolean[][] roi = new boolean[h][w];

        for (int y = 0; y < h; y += block) {
            for (int x = 0; x < w; x += block) {

                double mean = 0, var = 0;
                int count = 0;

                for (int i = 0; i < block && y + i < h; i++)
                    for (int j = 0; j < block && x + j < w; j++) {
                        mean += img[y + i][x + j];
                        count++;
                    }

                mean /= count;

                for (int i = 0; i < block && y + i < h; i++)
                    for (int j = 0; j < block && x + j < w; j++)
                        var += Math.pow(img[y + i][x + j] - mean, 2);

                var /= count;

                boolean isRoi = var > varThreshold;

                for (int i = 0; i < block && y + i < h; i++)
                    for (int j = 0; j < block && x + j < w; j++)
                        roi[y + i][x + j] = isRoi;
            }
        }
        return roi;
    }


    static int[][] applyROIMask(int[][] img, boolean[][] roi) {

        int h = img.length, w = img[0].length;
        int[][] out = new int[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                out[y][x] = roi[y][x] ? img[y][x] : 0;

        return out;
    }
}
