package Enhancement;

import java.util.ArrayList;
import java.util.List;

public class RidgeFrequency {
    static double[][] computeRidgeFrequency(int[][] img,double[][] orientation,int block) {

        int h = img.length;
        int w = img[0].length;
        double[][] freq = new double[h][w];

        for (int y = block; y < h - block; y += block) {
            for (int x = block; x < w - block; x += block) {

                double theta = orientation[y][x] + Math.PI / 2.0;

                // Projection perpendiculaire aux crêtes
                int len = block;
                double[] proj = new double[len];

                for (int i = 0; i < len; i++) {
                    int xx = (int) (x + i * Math.cos(theta));
                    int yy = (int) (y + i * Math.sin(theta));

                    if (xx >= 0 && xx < w && yy >= 0 && yy < h)
                        proj[i] = img[yy][xx];
                }

                // Détection des pics
                List<Integer> peaks = new ArrayList<>();
                for (int i = 1; i < proj.length - 1; i++) {
                    if (proj[i] > proj[i - 1] && proj[i] > proj[i + 1]) {
                        peaks.add(i);
                    }
                }

                if (peaks.size() >= 2) {
                    double sumDist = 0;
                    for (int i = 1; i < peaks.size(); i++)
                        sumDist += peaks.get(i) - peaks.get(i - 1);

                    double avgDist = sumDist / (peaks.size() - 1);
                    double f = 1.0 / avgDist;
                    if (f < 0.05 || f > 0.25)
                        continue;
                    // Remplissage du bloc
                    for (int dy = 0; dy < block; dy++)
                        for (int dx = 0; dx < block; dx++)
                            if (y + dy < h && x + dx < w)
                                freq[y + dy][x + dx] = f;
                }
            }
        }

        return freq;
    }
}
