import java.awt.image.BufferedImage;

public class Normalisation {

    /**
     * Normalise une image en niveaux de gris.
     * Chaque pixel est transformé pour avoir une moyenne μ0 et un écart-type σ0.
     * 
     * @param img BufferedImage niveau de gris
     * @param desiredMean moyenne souhaitée (ex: 128)
     * @param desiredStd ecart-type souhaité (ex: 50)
     * @return matrice normalisée [0..255]
     */
    public static int[][] normalize(BufferedImage img, float desiredMean, float desiredStd) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] gray = new int[h][w];

        float mean = 0;
        float variance = 0;

        // calculer la luminance et la moyenne
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int lum = (int)(0.299*r + 0.587*g + 0.114*b);
                gray[y][x] = lum;
                mean += lum;
            }
        }
        mean /= (w * h);

        // calculer variance
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float diff = gray[y][x] - mean;
                variance += diff * diff;
            }
        }
        variance /= (w * h);
        float std = (float)Math.sqrt(variance);

        // normalisation
        int[][] norm = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float val = gray[y][x];
                if (val > mean)
                    val = desiredMean + desiredStd * (val - mean) / std;
                else
                    val = desiredMean - desiredStd * (mean - val) / std;

                // clamp [0..255]
                val = Math.min(255, Math.max(0, val));
                norm[y][x] = (int)val;
            }
        }

        return norm;
    }
}
