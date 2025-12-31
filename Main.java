import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main{
    public static void main (String[] args) throws IOException {
        // String path= "finger.BMP";
        // int height, width;
        // BufferedImage IMG = ImageIO.read(new File(path));
        // int[][] mat = imageConvertToMatrix(IMG);
// Lire l'image
BufferedImage img = ImageIO.read(new File("image.png"));

// Normalisation
int[][] norm = Normalisation.normalize(img, 128, 50);
BufferedImage normImg = Orientation.matrixToBufferedImage(norm);

// Calcul orientation
Orientation.OrientationResult res = Orientation.computeOrientation(normImg, 16);

// Calcul fréquence locale des crêtes
float[][] freq = RidgeFrequency.computeFrequency(norm, 32, 16, res.orientation);

// Visualisation
BufferedImage freqVis = RidgeFrequency.drawFrequencyField(norm, freq, 32, 16);
ImageIO.write(freqVis, "png", new File("frequency.png"));

        
        // height = mat.length;
        // width = mat[0].length;
        // float[][] mat1 = new float[height][width];
        
        // for(int i=0 ; i<mat.length;i++){
        //     for(int j=0;j<mat[0].length;j++){
                
        //             mat1[i][j] = (float)255 - mat[i][j];
                
        //     }
            
        // }
        // showImage(mat1, path);

        // float mean = meanCalcul(mat, height, width);
        // float var = varianceCalcul(mat, height, width, mean);
        
        //float [][] G = normalizationOfImage(mat, height, width, mean, var);
        //showImage(mat, "normalizationOfImage");
        // float [][] O = orientationImage(mat, height, width);
        // showOrientationImage(O, "orientationImage");
        // float [][] F = ridgeFrequencyImage(O, G, mean, var, height, width);
        // showImage(scaleFrequencyForDisplay(F), "Frequency (debug)");
        // int [][] M = regionMask(F, height, width);
        // float [][] E = gaborEnhancement(G, O, F,M, height, width);
        // showCenteredImage(E, "Enhanced Image");
       
    }

    public static float[][] scaleFrequencyForDisplay(float[][] F) {
    int h = F.length, w = F[0].length;
    float[][] out = new float[h][w];

    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (F[i][j] <= 0)
                out[i][j] = 0;
            else
                out[i][j] = (F[i][j] - 1f/25f) / ((1f/3f)-(1f/25f)) * 255f;
        }
    }
    return out;
}


    public static int[][] imageConvertToMatrix(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();

        int[][] matrix = new int [height][width];

        for(int y = 0 ; y < height ;y++){
            for(int x = 0; x < width; x++){
                int gray = img.getRGB(x,y) & 0xFF; //on suppose que l'image est coder sur 8bit
                matrix[y][x]= gray;
            }
        }

        return matrix;
    }
    

    public static void showImage(float [][] matrix, String title) {
        int height = matrix.length;
        int width = matrix[0].length;

        // Création de l'image
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = Math.round(matrix[y][x]);
                if (value < 0) value = 0;
                if (value > 255) value = 255;

                int rgb = (value << 16) | (value << 8) | value;
                img.setRGB(x, y, rgb);
            }
        }

        // Affichage dans une fenêtre
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showOrientationImage(float[][] matrix, String title) {
    int height = matrix.length;
    int width = matrix[0].length;

    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {

            float theta = matrix[y][x]; // [-π/2 ; +π/2]

            // Normalisation vers [0 ; 1]
            float normalized = (theta + (float) Math.PI / 2f) / (float) Math.PI;

            // Conversion en niveaux de gris
            int value = Math.round(normalized * 255f);

            if (value < 0) value = 0;
            if (value > 255) value = 255;

            int rgb = (value << 16) | (value << 8) | value;
            img.setRGB(x, y, rgb);
        }
    }

    // Affichage (selon ton framework habituel)
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(new JLabel(new ImageIcon(img)));
    frame.pack();
    frame.setVisible(true);
}

public static void showCenteredImage(float[][] img, String title) {
    int h = img.length, w = img[0].length;

    float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
    for (int y = 0; y < h; y++)
        for (int x = 0; x < w; x++) {
            min = Math.min(min, img[y][x]);
            max = Math.max(max, img[y][x]);
        }

    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

    for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            int v = (int) ((img[y][x] - min) / (max - min) * 255f);
            v = Math.max(0, Math.min(255, v));
            int rgb = (v << 16) | (v << 8) | v;
            out.setRGB(x, y, rgb);
        }
    }

    JFrame f = new JFrame(title);
    f.add(new JLabel(new ImageIcon(out)));
    f.pack();
    f.setVisible(true);
}


    public static float meanCalcul(int[][] matrix, int height, int width){
        float mean = 0;

        for(int [] i : matrix ){
            for(int j : i){
                mean += j;
            } 
        }
        mean = mean * 1/(height*width);
        return mean;
    }

    public static float varianceCalcul(int[][] matrix, int height, int width, float mean){
        float var = 0;

        for(int [] i : matrix ){
            for(int j : i){
                var += Math.pow(j - mean,2);
            } 
        }

        var = var * 1/(height*width);

        return var;
    }

    public static float [][] normalizationOfImage(int [][] matrix ,int height, int width , float mean, float var){
         final float m0 = 100;
         final float var0 = 100;
        float [][] G = new float [height][width];

        int r = 0;
        for(int [] i : matrix ){
            int c = 0;
            for(int j : i){
              
               if (j > mean ){
                G[r][c] = m0 +  (float) Math.sqrt( var0 * ( Math.pow(j-mean,2) )/var );
               }
               else{
                G[r][c] = m0 -  (float) Math.sqrt( var0 * ( Math.pow(j-mean,2) ) /var);
               }
               c++;
            } 
            r++;
        }

        return G;
    }

    public static float [][] orientationImage(int [][] G,int height, int width){
        float [][] O = new float[height][width];
        float [][] thetaTab = new float[height][width];
        float [][] phiX = new float[height][width];
        float [][] phiY = new float[height][width];
        float [][] phiXPrime = new float[height][width];
        float [][] phiYPrime = new float[height][width];

        double[][] W = {
            {2, 4, 5, 4, 2},
            {4, 9, 12, 9, 4},
            {5, 12, 15, 12, 5},
            {4, 9, 12, 9, 4},
            {2, 4, 5, 4, 2}
        };
        double norm = 1.0 / 159.0;
        int k = 2; 
        // Divide G into blocks of size w x w (16 x 16)
        final int blockSize = 16;
        for(int by = 0; by < height; by+=blockSize){
            for(int bx = 0; bx < width; bx += blockSize){
                //real dimension
                int bh = Math.min(blockSize, height - by);
                int bw = Math.min(blockSize, width - bx);

                float Vx = 0;
                float Vy = 0;

                //Gradient calculate
                for(int y = 1 ;  y <bh - 1 ; y++){
                    for(int x = 1 ; x < bw - 1; x++){
                        int py = by + y ;
                        int px = bx + x ;

                        //dx
                        float dx = -1*G[py-1][px-1] + 0*G[py-1][px] + 1*G[py-1][px+1] +
                        -2*G[py][px-1]   + 0*G[py][px]   + 2*G[py][px+1] +
                        -1*G[py+1][px-1] + 0*G[py+1][px] + 1*G[py+1][px+1];

                        float dy =  -1*G[py-1][px-1] + -2*G[py-1][px] + -1*G[py-1][px+1] +
                         0*G[py][px-1]    +  0*G[py][px]    +  0*G[py][px+1] +
                         1*G[py+1][px-1]  +  2*G[py+1][px]  +  1*G[py+1][px+1];

                         Vx += 2 * dx * dy;
                         Vy += (dx * dx) - (dy * dy);
                    }
                } 
                float theta = 0.5f * (float) Math.atan2(Vy,Vx);

                for (int y = 0; y < bh; y++) {
                    for (int x = 0; x < bw; x++) {
                        thetaTab[by + y][bx + x] = theta;
                    }
                }
            }
        }

        int r = 0;
        for(float [] i : thetaTab ){
            int c = 0;
            for(float j : i){
              phiX[r][c] = (float) Math.cos(2*j);
              phiY[r][c] = (float) Math.sin(2*j); 
              c++;
            } 
            r++;
        }
       
        for(int i  = 0; i< height; i++){
            for(int j = 0; j < width; j++){
                float sumX = 0.f;
                float sumY = 0.f;
                for (int u = -k; u <= k; u++) {
                    for (int v = -k; v <= k; v++) {

                        int x = i + u;
                        int y = j + v;

                        // Bord : on "clamp" dans l’image
                        if (x < 0) x = 0;
                        if (x >= height) x = height - 1;
                        if (y < 0) y = 0;
                        if (y >= width) y = width - 1;

                        sumX += norm * W[u + k][v + k] * phiX[x][y];
                        sumY += norm * W[u + k][v + k] * phiY[x][y];
                    }
                }

                phiXPrime[i][j] = (float) sumX;
                phiYPrime[i][j] = (float) sumY;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                O[i][j] = 0.5f * (float) Math.atan2(phiYPrime[i][j], phiXPrime[i][j]);
            }
        }
        return O;
    }

    private static int BLOCK_SIZE = 16;
    private static int WINDOW_HEIGHT = 16;
    private static int WINDOW_WIDTH = 32;
    public static float[][] ridgeFrequencyImage(
            float[][] orientationImage,
            float [][] normalizationOfImage,
            float mean,
            float var,
            int height,
            int width
    ) {
        final int BLOCK = 16;
        final int L = 32;
        final float FREQ_MIN = 1.0f / 25.0f;
        final float FREQ_MAX = 1.0f / 3.0f;

        float[][] W = new float[height][width];

        // Initialisation à -1 (fréquence invalide)
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                W[i][j] = -1f;

        /* =========================
        Étapes 1 à 5 (article 2.5)
        ========================= */
        for (int i = BLOCK / 2; i < height; i += BLOCK) {
            for (int j = BLOCK / 2; j < width; j += BLOCK) {

                float[] xSignature = new float[L];

                for (int k = 0; k < L; k++) {
                    float sum = 0f;

                    for (int d = 0; d < BLOCK; d++) {

                        int u = Math.round(
                                i
                                + (d - BLOCK / 2f) * (float) Math.cos(orientationImage[i][j])
                                + (k - L / 2f) * (float) Math.sin(orientationImage[i][j])
                        );

                        int v = Math.round(
                                j
                                + (d - BLOCK / 2f) * (float) Math.sin(orientationImage[i][j])
                                - (k - L / 2f) * (float) Math.cos(orientationImage[i][j])
                        );

                        if (u >= 0 && u < height && v >= 0 && v < width)
                            sum += normalizationOfImage[u][v];
                    }
                    xSignature[k] = sum;
                }

                // Détection des pics (distance moyenne)
                int lastPeak = -1;
                float sumDist = 0f;
                int count = 0;

                for (int k = 1; k < L - 1; k++) {
                    if (xSignature[k] > xSignature[k - 1]
                            && xSignature[k] > xSignature[k + 1] && xSignature[k] > mean + 0.2 * var) {

                        if (lastPeak != -1) {
                            sumDist += (k - lastPeak);
                            count++;
                        }
                        lastPeak = k;
                    }
                }

                if (count > 0) {
                    float T = sumDist / count;
                    float freq = 1.0f / T;

                    if (freq >= FREQ_MIN && freq <= FREQ_MAX)
                        W[i][j] = freq;
                    }
                }
        }

        /* =========================
        Étape 6 : interpolation
        ========================= */
        boolean hasInvalid;
        do {
            hasInvalid = false;
            float[][] Wnew = new float[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    if (W[i][j] != -1) {
                        Wnew[i][j] = W[i][j];
                        continue;
                    }

                    float sum = 0f;
                    float weight = 0f;

                    for (int u = -3; u <= 3; u++) {
                        for (int v = -3; v <= 3; v++) {
                            int x = i + u;
                            int y = j + v;

                            if (x >= 0 && x < height && y >= 0 && y < width && W[x][y] != -1) {
                                float g = (float) Math.exp(-(u * u + v * v) / 18.0);
                                sum += g * W[x][y];
                                weight += g;
                            }
                        }
                    }

                    if (weight > 0) {
                        Wnew[i][j] = sum / weight;
                    } else {
                        Wnew[i][j] = -1;
                        hasInvalid = true;
                    }
                }
            }
            W = Wnew;

        } while (hasInvalid);

        /* =========================
        Étape 7 : lissage final
        ========================= */
        float[][] F = new float[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                float sum = 0f;
                float weight = 0f;

                for (int u = -3; u <= 3; u++) {
                    for (int v = -3; v <= 3; v++) {
                        int x = i + u;
                        int y = j + v;

                        if (x >= 0 && x < height && y >= 0 && y < width) {
                            float g = (float) Math.exp(-(u * u + v * v) / 18.0);
                            sum += g * W[x][y];
                            weight += g;
                        }
                    }
                }
                F[i][j] = sum / weight;
            }
        }

        return F;
    }

    public static int[][] regionMask(
        float[][] frequencyImage,
        int height,
        int width) {

    int BLOCK = 16;
    int[][] mask = new int[height][width];

    for (int y = 0; y < height; y += BLOCK) {
        for (int x = 0; x < width; x += BLOCK) {

            float freq = frequencyImage[y][x];

            if (freq <= 0) continue;

            for (int j = y; j < y + BLOCK && j < height; j++) {
                for (int i = x; i < x + BLOCK && i < width; i++) {
                    mask[j][i] = 1;
                }
            }
        }
    }
    return mask;
}


public static float[][] gaborEnhancement(
        float[][] normalizedImage,
        float[][] orientationImage,
        float[][] frequencyImage,
        int[][] regionMask,
        int height,
        int width) {

    final int FILTER_SIZE = 9;        // plus stable que 11
    final int HALF = FILTER_SIZE / 2;
    final double sigma = 3.0;         // dx = dy = 3 (réduit le bruit)

    float[][] enhanced = new float[height][width];

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {

            // 🔴 Ne pas filtrer le fond
            if (regionMask[y][x] == 0) {
                enhanced[y][x] = normalizedImage[y][x];
                continue;
            }

            float freq = frequencyImage[y][x];
            if (freq <= 0) {
                enhanced[y][x] = normalizedImage[y][x];
                continue;
            }

            float theta = orientationImage[y][x];
            double cosT = Math.cos(theta);
            double sinT = Math.sin(theta);

            double sum = 0.0;
            double norm = 0.0;

            for (int j = -HALF; j <= HALF; j++) {
                for (int i = -HALF; i <= HALF; i++) {

                    int px = x + i;
                    int py = y + j;

                    if (px < 0 || py < 0 || px >= width || py >= height)
                        continue;

                    // Coordonnées tournées (équations 19–20)
                    double xPrime =  i * cosT + j * sinT;
                    double yPrime = -i * sinT + j * cosT;

                    // Enveloppe gaussienne
                    double gaussian = Math.exp(
                            -(xPrime * xPrime + yPrime * yPrime) /
                            (2.0 * sigma * sigma)
                    );

                    // Onde cosinus (équation 18)
                    double wave = Math.cos(2.0 * Math.PI * freq * xPrime);

                    double gabor = gaussian * wave;

                    sum  += normalizedImage[py][px] * gabor;
                    norm += Math.abs(gabor);
                }
            }

            // Sécurité numérique
            if (norm < 1e-6) {
                enhanced[y][x] = normalizedImage[y][x];
            } else {
                enhanced[y][x] = (float) (sum / norm);
            }
        }
    }

    return enhanced;
}


}