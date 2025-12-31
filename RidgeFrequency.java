import java.awt.Color;
import java.awt.image.BufferedImage;

public class RidgeFrequency {

    /**
     * Calcule la fréquence locale des crêtes pour chaque bloc de l'image.
     * 
     * @param img image normalisée (matrice de niveaux de gris 0-255)
     * @param blockHeight hauteur du bloc (ex: 16)
     * @param blockWidth largeur du bloc (ex: 32)
     * @param orientation  matrice d'orientation locale (en radians)
     * @return matrice des fréquences locales (0 si non calculable)
     */
    public static float[][] computeFrequency(
        int[][] img,
        int blockWidth,
        int blockHeight,
        float[][] orientation) {

    int h = img.length;
    int w = img[0].length;

    int blocksY = h / blockHeight;
    int blocksX = w / blockWidth;

    float[][] frequency = new float[blocksY][blocksX];

    for (int by = 0; by < blocksY; by++) {
        for (int bx = 0; bx < blocksX; bx++) {

            // centre du bloc (pixels)
            int cx = bx * blockWidth + blockWidth / 2;
            int cy = by * blockHeight + blockHeight / 2;

            // orientation DU BLOC (✔ correction ici)
            float theta = orientation[by][bx];

            float cosT = (float) Math.cos(theta);
            float sinT = (float) Math.sin(theta);

            float[] xSignature = new float[blockWidth];

            for (int x = 0; x < blockWidth; x++) {
                float sum = 0;

                for (int y = 0; y < blockHeight; y++) {

                    int dx = x - blockWidth / 2;
                    int dy = y - blockHeight / 2;

                    int px = (int) (cx + dx * cosT - dy * sinT);
                    int py = (int) (cy + dx * sinT + dy * cosT);

                    if (px >= 0 && px < w && py >= 0 && py < h) {
                        sum += img[py][px];
                    }
                }
                xSignature[x] = sum / blockHeight;
            }

            float avgDist = averageDistanceBetweenPeaks(xSignature);
            frequency[by][bx] = (avgDist > 0) ? (1.0f / avgDist) : 0;
        }
    }
    return frequency;
}


    /**
     * Détecte les pics dans un tableau 1D et retourne la distance moyenne entre eux.
     */
    private static float averageDistanceBetweenPeaks(float[] sig) {
        int n = sig.length;
        java.util.List<Integer> peaks = new java.util.ArrayList<>();

        for (int i = 1; i < n - 1; i++) {
            if (sig[i] > sig[i - 1] && sig[i] > sig[i + 1]) {
                peaks.add(i);
            }
        }

        if (peaks.size() < 2)
            return -1;

        float sum = 0;
        for (int i = 1; i < peaks.size(); i++)
            sum += peaks.get(i) - peaks.get(i - 1);

        return sum / (peaks.size() - 1);
    }

    /**
     * Visualisation simple de la fréquence locale (pour debug)
     */
    public static BufferedImage drawFrequencyField(int[][] img, float[][] freq, int blockWidth, int blockHeight) {
        int h = img.length;
        int w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int val = img[y][x];
                out.setRGB(x, y, (val << 16) | (val << 8) | val);
            }
        }

        java.awt.Graphics2D g2 = out.createGraphics();
        g2.setColor(Color.RED);

        for (int by = 0; by < freq.length; by++) {
            for (int bx = 0; bx < freq[0].length; bx++) {
                int cx = bx * blockWidth + blockWidth / 2;
                int cy = by * blockHeight + blockHeight / 2;

                float f = freq[by][bx];
                if (f > 0) {
                    // longueur du trait proportionnelle à la fréquence
                    int len = (int)(blockWidth * 0.5f);
                    g2.drawLine(cx - len, cy, cx + len, cy);
                }
            }
        }

        g2.dispose();
        return out;
    }
}
