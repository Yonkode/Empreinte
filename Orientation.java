import java.awt.*;
import java.awt.image.BufferedImage;

public class Orientation {

    /* =======================
       Structure de résultat
       ======================= */
    public static class OrientationResult {
        public float[][] orientation; // radians [0..PI[
        public float[][] coherence;   // [0..1]
    }

    /* =======================
       Masques Sobel
       ======================= */
    private static final int[][] SOBEL_X = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    private static final int[][] SOBEL_Y = {
            { 1,  2,  1},
            { 0,  0,  0},
            {-1, -2, -1}
    };

    /* =======================
       BufferedImage -> Matrice gris
       ======================= */
    public static int[][] bufferedImageToGrayMatrix(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] gray = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                gray[y][x] = (int)(0.299*r + 0.587*g + 0.114*b);
            }
        }
        return gray;
    }

    /* =======================
   Convertir matrice gris -> BufferedImage
   ======================= */
public static BufferedImage matrixToBufferedImage(int[][] gray) {
    int h = gray.length;
    int w = gray[0].length;

    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

    for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            int val = gray[y][x];
            val = Math.max(0, Math.min(255, val)); // clamp 0-255
            int rgb = (val << 16) | (val << 8) | val;
            img.setRGB(x, y, rgb);
        }
    }
    return img;
}

    /* =======================
       Calcul des gradients
       ======================= */
    private static void computeGradients(
            int[][] img, float[][] gx, float[][] gy) {

        int h = img.length;
        int w = img[0].length;

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {

                float sx = 0, sy = 0;

                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int v = img[y + j][x + i];
                        sx += v * SOBEL_X[j + 1][i + 1];
                        sy += v * SOBEL_Y[j + 1][i + 1];
                    }
                }
                gx[y][x] = sx;
                gy[y][x] = sy;
            }
        }
    }

    /* =======================
       Calcul orientation + cohérence
       ======================= */
    public static OrientationResult computeOrientation(
            BufferedImage img, int blockSize) {

        int[][] gray = bufferedImageToGrayMatrix(img);

        int h = gray.length;
        int w = gray[0].length;

        float[][] gx = new float[h][w];
        float[][] gy = new float[h][w];

        computeGradients(gray, gx, gy);

        int bh = h / blockSize;
        int bw = w / blockSize;

        OrientationResult res = new OrientationResult();
        res.orientation = new float[bh][bw];
        res.coherence   = new float[bh][bw];

        for (int by = 0; by < bh; by++) {
            for (int bx = 0; bx < bw; bx++) {

                float Gxx = 0, Gyy = 0, Gxy = 0;

                for (int y = by * blockSize; y < (by + 1) * blockSize; y++) {
                    for (int x = bx * blockSize; x < (bx + 1) * blockSize; x++) {
                        float dx = gx[y][x];
                        float dy = gy[y][x];
                        Gxx += dx * dx;
                        Gyy += dy * dy;
                        Gxy += dx * dy;
                    }
                }

                float theta = 0.5f * (float)Math.atan2(
                        2 * Gxy, Gxx - Gyy
                );

                float denom = Gxx + Gyy;
                float coherence = (denom == 0) ? 0 :
                        (float)Math.sqrt(
                                (Gxx - Gyy)*(Gxx - Gyy) + 4*Gxy*Gxy
                        ) / denom;

                res.orientation[by][bx] = theta;
                res.coherence[by][bx]   = coherence;
            }
        }
        return res;
    }

    /* =======================
       Visualisation (traits)
       ======================= */
    public static BufferedImage drawOrientationField(
            BufferedImage img,
            OrientationResult res,
            int blockSize,
            float coherenceThreshold) {

        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(img, 0, 0, null);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(1));

        int bh = res.orientation.length;
        int bw = res.orientation[0].length;

        int half = blockSize / 2;
        int len  = blockSize / 2;

        for (int by = 0; by < bh; by++) {
            for (int bx = 0; bx < bw; bx++) {

                if (res.coherence[by][bx] < coherenceThreshold)
                    continue;

                float theta = res.orientation[by][bx];

                int cx = bx * blockSize + half;
                int cy = by * blockSize + half;

                int dx = (int)(len * Math.cos(theta));
                int dy = (int)(len * Math.sin(theta));

                g2d.drawLine(cx - dx, cy - dy, cx + dx, cy + dy);
            }
        }

        g2d.dispose();
        return out;
    }
}
