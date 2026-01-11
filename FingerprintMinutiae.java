import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import  org.apache.pdfbox.pdmodel.PDDocument;


public class FingerprintMinutiae {

    static class Minutia {
        enum Type { RIDGE_ENDING, BIFURCATION }
        int x, y;
        double angle;
        Type type;

        Minutia(int x, int y, double angle, Type type) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.type = type;
        }
    }

    static class TransformKey {
    int dx, dy, dTheta;

    TransformKey(int dx, int dy, int dTheta) {
        this.dx = dx;
        this.dy = dy;
        this.dTheta = dTheta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dx, dy, dTheta);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransformKey)) return false;
        TransformKey k = (TransformKey) o;
        return dx == k.dx && dy == k.dy && dTheta == k.dTheta;
    }
}

static class RocPoint {
    double threshold;
    double far;
    double frr;

    RocPoint(double threshold, double far, double frr) {
        this.threshold = threshold;
        this.far = far;
        this.frr = frr;
    }
}

static String identify1N(
        List<Minutia> query,
        Map<String, List<Minutia>> gallery,
        double threshold) {

    String bestId = null;
    double bestScore = 0;

    for (Map.Entry<String, List<Minutia>> e : gallery.entrySet()) {
        double score = matchMinutiaeHough(query, e.getValue());

        if (score > bestScore) {
            bestScore = score;
            bestId = e.getKey();
        }
    }

    if (bestScore >= threshold)
        return bestId;
    else
        return "REJECT";
}
    public static void main(String[] args) throws Exception {

        int[][] image = loadGrayscaleImage("C:\\Programmation\\Java\\Java\\Empreinte\\fingerprints\\DB1_B\\101_1.tif");
        // int[][] image1 = loadGrayscaleImage("101_1.tif");
        // int[][] image2 = loadGrayscaleImage("101_1r1.tif");
        // int[][] image3 = loadGrayscaleImage("101_1re.tif");

         extractMinutiaeWithShowImage(image);
        // extractMinutiaeWithShowImage(image1);
        // extractMinutiaeWithShowImage(image2);
        // extractMinutiaeWithShowImage(image3);
        //evaluateDB1_B("C:\\Programmation\\Java\\Java\\Empreinte\\fingerprints\\DB1_B");

        //identify1NFromImage("101_1.tif","C:\\Programmation\\Java\\Java\\Empreinte\\fingerprints\\DB1_B",0.260);
        // matching(image1, image);
        // matching(image1, image2);
        // matching(image1, image3);
    }

    static void identify1NFromImage(String queryPath,String galleryPath,double threshold) throws Exception {

        List<Minutia> query = extractMinutiae(
                loadGrayscaleImage(queryPath));

        Map<String, List<Minutia>> gallery = new HashMap<>();

        for (File f : new File(galleryPath).listFiles()) {
            if (!f.getName().endsWith(".tif")) continue;

            String id = f.getName().split("_")[0];
            gallery.put(id, extractMinutiae(
                    loadGrayscaleImage(f.getAbsolutePath())));
        }

        String bestId = null;
        double bestScore = 0;

        for (String id : gallery.keySet()) {
            double s = matchMinutiaeHough(query, gallery.get(id));
            if (s > bestScore) {
                bestScore = s;
                bestId = id;
            }
        }

        // 🔹 Décision
        System.out.println("Best ID = " + bestId);
        System.out.println("Score = " + bestScore);

        if (bestScore >= threshold)
            System.out.println(" IDENTIFIÉ");
        else
            System.out.println(" NON PRÉSENT");
    }

    static void matching(int[][] image, int[][] image2) throws Exception {
        image = normalize(image, 128, 128);
        double[][] orientation = computeOrientation(image, 16);
        double[][] frequency2 = computeRidgeFrequency(
        image, orientation, 16);
        image = gaborEnhance(image, orientation, frequency2);
        int[][] binary = adaptiveBinarize(image, 16);
        int[][] thin = thinning(binary);

        image2 = normalize(image2, 128, 128);
        double[][] orientation2 = computeOrientation(image2, 16);
        double[][] frequency = computeRidgeFrequency(
        image, orientation, 16);
        image2 = gaborEnhance(image2, orientation2, frequency);
        int[][] binary2 = adaptiveBinarize(image2, 16);
        int[][] thin2 = thinning(binary2);
    
        List<Minutia> mins = extractMinutiae(thin, orientation);
        mins = filterBorderMinutiae(mins,
                image[0].length, image.length);
        mins = filterByOrientationConsistency(mins,
                orientation, 30);
        mins = filterMinutiaeStrong(mins);

        List<Minutia> mins2 = extractMinutiae(thin2, orientation2);
        mins2 = filterBorderMinutiae(mins2,
                image2[0].length, image2.length);
        mins2 = filterByOrientationConsistency(mins2,
                orientation2, 30);
        mins2 = filterMinutiaeStrong(mins2);
        System.out.println("Minuties finales = " + mins.size());
        System.out.println("Minuties finales = " + mins2.size());
        double score = matchMinutiaeHough(mins, mins2);
        System.out.println("Score de similarité = " + score);
    }

    static List<Minutia> extractMinutiaeWithShowImage(int[][] image)
        throws Exception {

        // 1️⃣ Normalisation
        image = normalize(image, 128, 128);
        showGrayImage(image, "1 - Normalisation");

        //  ROI
        //boolean[][] roi = computeROIMask(image, 16, 150.0);
        //showROIMask(image, roi);

        // Orientation
        double[][] orientation = computeOrientation(image, 16);
        showOrientation(orientation, 16);
        double[][] frequency = computeRidgeFrequency(
        image, orientation, 16);

        image = gaborEnhance(image, orientation, frequency);
        showGrayImage(image, "2 - Gabor");

        // Binarisation + ROI
        int[][] binary = adaptiveBinarize(image, 16);
        // binary = applyROIMask(binary, roi);
        // showBinaryImage(binary, "3 - Binarisation");

        //  Squelettisation
        int[][] thin = thinning(binary);
        showBinaryImage(thin, "4 - Squelette");

        //  Extraction
        List<Minutia> mins = extractMinutiae(thin, orientation);

        //  Filtrages critiques
        mins = filterBorderMinutiae(mins,
                image[0].length, image.length);
        mins = filterByOrientationConsistency(mins,
                orientation, 30);
        mins = filterMinutiaeStrong(mins);

        // 9️⃣ Résultat final
        showMinutiae(image, mins);
        System.out.println("Minuties finales = " + mins.size());

        return mins;
    }


    static List<Minutia> extractMinutiaeFromFile(File f) throws Exception {

        int[][] img = normalize(loadGrayscaleImage(f.getAbsolutePath()), 128, 128);
        double[][] ori = computeOrientation(img, 16);
        double[][] frequency = computeRidgeFrequency(
            img, ori, 16);
        img = gaborEnhance(img, ori, frequency);
        int[][] bin = adaptiveBinarize(img, 16);
        int[][] thin = thinning(bin);

        return filterMinutiaeStrong(extractMinutiae(thin, ori));
    }

    static List<Minutia> extractMinutiae(int[][] image) throws Exception {

        image = normalize(image, 128, 128);

        //boolean[][] roi = computeROIMask(image, 16, 150.0); // ⚠️ important

        double[][] orientation = computeOrientation(image, 16);
        double[][] frequency = computeRidgeFrequency(
        image, orientation, 16);
    
        image = gaborEnhance(image, orientation, frequency);
        int[][] binary = adaptiveBinarize(image, 16);
        //binary = applyROIMask(binary, roi);

    
        int[][] thin = thinning(binary);

    
        List<Minutia> minutiae = new ArrayList<>();

        int h = thin.length, w = thin[0].length;
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                if (thin[y][x] == 1) {
                    int cn = crossingNumber(neighbors(thin, x, y));
                    if (cn == 1 || cn == 3) {

                        // 🔥 orientation lissée
                        double angle = smoothOrientation(orientation, x, y, 2);

                        minutiae.add(new Minutia(
                                x, y,
                                angle,
                                cn == 1 ? Minutia.Type.RIDGE_ENDING
                                        : Minutia.Type.BIFURCATION
                        ));
                    }
                }
            }
        }

        minutiae = filterBorderMinutiae(minutiae, w, h);
        minutiae = filterByOrientationConsistency(minutiae, orientation, 30);
        minutiae = filterMinutiaeStrong(minutiae);

        return minutiae;
    }

    static int countRidgePixels(int[][] img, int x, int y, int r) {
        int count = 0;
        for (int i = -r; i <= r; i++)
            for (int j = -r; j <= r; j++)
                if (img[y + i][x + j] == 1)
                    count++;
        return count;
    }

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

    static List<Minutia> extractMinutiaeFullPipeline(int[][] image) throws Exception {

        /* 1️⃣ NORMALISATION */
        image = normalize(image, 128, 128);

        /* 2️⃣ ROI (PRIORITÉ ABSOLUE) */
        boolean[][] roi = computeROIMask(image, 16, 300.0);

        /* 3️⃣ ORIENTATION */
        double[][] orientation = computeOrientation(image, 16);
        double[][] frequency = computeRidgeFrequency(
            image, orientation, 16);
        /* 4️⃣ AMÉLIORATION GABOR */
        image = gaborEnhance(image, orientation, frequency);

        /* 5️⃣ BINARISATION + ROI */
        int[][] binary = adaptiveBinarize(image, 16);
        binary = applyROIMask(binary, roi);

        /* 6️⃣ SQUELETTISATION */
        int[][] thin = thinning(binary);

        /* 7️⃣ EXTRACTION DES MINUTIES */
        List<Minutia> minutiae = extractMinutiae(thin, orientation);

        /* 8️⃣ FILTRAGES CRITIQUES */
        minutiae = filterBorderMinutiae(
                minutiae, image[0].length, image.length);

        minutiae = filterByOrientationConsistency(
                minutiae, orientation, 30);

        minutiae = filterMinutiaeStrong(minutiae);

        return minutiae;
    }


    static void evaluateDB1_B(String basePath) throws Exception {

        List<Double> genuine = new ArrayList<>();
        List<Double> impostor = new ArrayList<>();

        Map<String, List<List<Minutia>>> db = new HashMap<>();

        for (int id = 101; id <= 110; id++) {

            List<List<Minutia>> samples = new ArrayList<>();

            for (int k = 1; k <= 8; k++) {
                String path = basePath + "\\" + id + "_" + k + ".tif";

                int[][] img = loadGrayscaleImage(path);
                List<Minutia> mins = extractMinutiae(img);
                samples.add(mins);
            }

            db.put(String.valueOf(id), samples);
        }

        for (String id : db.keySet()) {
            List<List<Minutia>> s = db.get(id);
            for (int i = 1; i < s.size(); i++) {
                genuine.add(matchMinutiaeHough(s.get(0), s.get(i)));
            }
        }

        List<String> ids = new ArrayList<>(db.keySet());
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                impostor.add(matchMinutiaeHough(
                        db.get(ids.get(i)).get(0),
                        db.get(ids.get(j)).get(0)));
            }
        }

        List<RocPoint> roc = computeROC(genuine, impostor, 200);
        double eer = computeEER(roc);

        System.out.println("=== DB1_B Evaluation ===");
        System.out.println("Genuine  = " + genuine.size());
        System.out.println("Impostor = " + impostor.size());
        System.out.printf("EER = %.2f %%\n", eer * 100);

        saveROC(roc, "roc_db1.csv");
    }



    static void showROIMask(int[][] img, boolean[][] roi) {

        int h = img.length, w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++) {
                int v = img[y][x];
                int gray = (v << 16) | (v << 8) | v;

                if (!roi[y][x])
                    out.setRGB(x, y, Color.RED.getRGB());
                else
                    out.setRGB(x, y, gray);
            }
        }
        showImage(out, "ROI (zone utile en gris)");
    }
    
    static int[][] loadGrayscaleImage(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        int w = img.getWidth(), h = img.getHeight();
        int[][] gray = new int[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                gray[y][x] = (r + g + b) / 3;
            }
        return gray;
    }

    static int[][] normalize(int[][] img, int targetMean, int targetVar) {
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

    static List<Minutia> filterBorderMinutiae(
            List<Minutia> mins, int w, int h) {

        int BORDER = 20;
        List<Minutia> out = new ArrayList<>();

        for (Minutia m : mins) {
            if (m.x > BORDER && m.y > BORDER &&
                m.x < w - BORDER && m.y < h - BORDER)
                out.add(m);
        }
        return out;
    }

    static List<Minutia> filterByOrientationConsistency(
            List<Minutia> mins,
            double[][] ori,
            double maxDiffDeg) {

        List<Minutia> out = new ArrayList<>();
        double maxDiff = Math.toRadians(maxDiffDeg);

        for (Minutia m : mins) {
            double diff = Math.abs(m.angle - ori[m.y][m.x]);
            if (diff < maxDiff)
                out.add(m);
        }
        return out;
    }

    static double smoothOrientation(double[][] ori, int x, int y, int r) {
        double sx = 0, sy = 0;
        for (int i = -r; i <= r; i++)
            for (int j = -r; j <= r; j++) {
                int yy = Math.max(0, Math.min(ori.length - 1, y + i));
                int xx = Math.max(0, Math.min(ori[0].length - 1, x + j));
                sx += Math.cos(ori[yy][xx]);
                sy += Math.sin(ori[yy][xx]);
            }
        return Math.atan2(sy, sx);
    }

    static int[][] thinning(int[][] img) {
        int h = img.length, w = img[0].length;
        boolean changed;

        do {
            changed = false;
            for (int pass = 0; pass < 2; pass++) {
                int[][] mark = new int[h][w];
                for (int y = 1; y < h - 1; y++)
                    for (int x = 1; x < w - 1; x++)
                        if (img[y][x] == 1) {
                            int[] p = neighbors(img, x, y);
                            int A = transitions(p);
                            int B = sum(p);

                            if (A == 1 && B >= 2 && B <= 6) {
                                if (pass == 0 && p[0] * p[2] * p[4] == 0 && p[2] * p[4] * p[6] == 0)
                                    mark[y][x] = 1;
                                if (pass == 1 && p[0] * p[2] * p[6] == 0 && p[0] * p[4] * p[6] == 0)
                                    mark[y][x] = 1;
                            }
                        }
                for (int y = 0; y < h; y++)
                    for (int x = 0; x < w; x++)
                        if (mark[y][x] == 1) {
                            img[y][x] = 0;
                            changed = true;
                        }
            }
        } while (changed);

        return img;
    }

    static List<Minutia> extractMinutiae(int[][] img, double[][] ori) {
        List<Minutia> list = new ArrayList<>();
        int h = img.length, w = img[0].length;

        for (int y = 1; y < h - 1; y++)
            for (int x = 1; x < w - 1; x++)
                if (img[y][x] == 1) {
                    int cn = crossingNumber(neighbors(img, x, y));
                    if (cn == 1)
                        list.add(new Minutia(x, y, ori[y][x], Minutia.Type.RIDGE_ENDING));
                    else if (cn == 3)
                        list.add(new Minutia(x, y, ori[y][x], Minutia.Type.BIFURCATION));
                }
        return list;
    }

    static int[] neighbors(int[][] img, int x, int y) {
        return new int[]{
            img[y - 1][x], img[y - 1][x + 1], img[y][x + 1], img[y + 1][x + 1],
            img[y + 1][x], img[y + 1][x - 1], img[y][x - 1], img[y - 1][x - 1]
        };
    }

    static int transitions(int[] p) {
        int t = 0;
        for (int i = 0; i < 8; i++)
            if (p[i] == 0 && p[(i + 1) % 8] == 1) t++;
        return t;
    }

    static int sum(int[] p) {
        int s = 0;
        for (int v : p) s += v;
        return s;
    }

    static int crossingNumber(int[] p) {
        int cn = 0;
        for (int i = 0; i < 8; i++)
            cn += Math.abs(p[i] - p[(i + 1) % 8]);
        return cn / 2;
    }

    static void showGrayImage(int[][] img, String title) {
        int h = img.length, w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int v = Math.max(0, Math.min(255, img[y][x]));
                int rgb = (v << 16) | (v << 8) | v;
                out.setRGB(x, y, rgb);
            }

        showImage(out, title);
    }

    static void showBinaryImage(int[][] img, String title) {
        int h = img.length, w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                out.setRGB(x, y, img[y][x] == 1 ? 0xFFFFFF : 0x000000);

        showImage(out, title);
    }

    static void showImage(BufferedImage img, String title) {
        javax.swing.JFrame frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new javax.swing.JLabel(new javax.swing.ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
    }


    static void saveImage(int[][] img, String filename) throws Exception {
        int h = img.length, w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int v = Math.max(0, Math.min(255, img[y][x]));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }

        ImageIO.write(out, "png", new File(filename));
    }

    static void showOrientation(double[][] ori, int step) {
        int h = ori.length, w = ori[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.RED);

        for (int y = step; y < h; y += step)
            for (int x = step; x < w; x += step) {
                double theta = ori[y][x];
                int x2 = (int)(x + 10 * Math.cos(theta));
                int y2 = (int)(y + 10 * Math.sin(theta));
                g.drawLine(x, y, x2, y2);
            }

        showImage(img, "Champ d'orientation");
    }

    static void showMinutiae(int[][] img, List<Minutia> mins) {
        int h = img.length, w = img[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int v = img[y][x];
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }

        Graphics2D g = out.createGraphics();
        for (Minutia m : mins) {
            g.setColor(m.type == Minutia.Type.RIDGE_ENDING ? Color.GREEN : Color.BLUE);
            g.fillOval(m.x - 3, m.y - 3, 6, 6);
        }

        showImage(out, "Minuties détectées");
    }


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


    static int[][] adaptiveBinarize(int[][] img, int block) {
        int h = img.length, w = img[0].length;
        int[][] bin = new int[h][w];

        for (int y = 0; y < h; y += block) {
            for (int x = 0; x < w; x += block) {

                int sum = 0, count = 0;
                for (int i = 0; i < block && y + i < h; i++)
                    for (int j = 0; j < block && x + j < w; j++) {
                        sum += img[y + i][x + j];
                        count++;
                    }

                int mean = sum / count;

                for (int i = 0; i < block && y + i < h; i++)
                    for (int j = 0; j < block && x + j < w; j++)
                        bin[y + i][x + j] = img[y + i][x + j] > mean ? 1 : 0;
            }
        }
        return bin;
    }

    static List<Minutia> filterMinutiaeStrong(List<Minutia> mins) {

        List<Minutia> out = new ArrayList<>();
        double MIN_DIST = 20;

        for (Minutia m : mins) {
            boolean keep = true;
            for (Minutia n : out) {
                if (Math.hypot(m.x - n.x, m.y - n.y) < MIN_DIST) {
                    keep = false;
                    break;
                }
            }
            if (keep) out.add(m);
        }

        out.sort(Comparator.comparingInt(a -> a.x));
        return out;
    }

    static double matchMinutiaeHough(List<Minutia> A, List<Minutia> B) {

        int BIN_XY = 10;                 // pixels
        int BIN_THETA = 10;              // degrés
        int MAX_PAIRS = 3000;            // sécurité

        Map<TransformKey, Integer> votes = new HashMap<>();

        int pairs = 0;

        for (Minutia a : A) {
            for (Minutia b : B) {

                if (a.type != b.type) continue;

                int dx = (int) Math.round((b.x - a.x) / (double) BIN_XY);
                int dy = (int) Math.round((b.y - a.y) / (double) BIN_XY);
                int dTheta = (int) Math.round(
                        Math.toDegrees(b.angle - a.angle) / BIN_THETA);

                TransformKey key = new TransformKey(dx, dy, dTheta);
                votes.put(key, votes.getOrDefault(key, 0) + 1);

                if (++pairs > MAX_PAIRS) break;
            }
            if (pairs > MAX_PAIRS) break;
        }

        // Trouver la meilleure transformation
        TransformKey best = null;
        int bestVotes = 0;

        for (Map.Entry<TransformKey, Integer> e : votes.entrySet()) {
            if (e.getValue() > bestVotes) {
                bestVotes = e.getValue();
                best = e.getKey();
            }
        }

        if (best == null) return 0;

        // Vérification fine
        int matches = 0;
        boolean[] used = new boolean[B.size()];

        for (Minutia a : A) {

            double x = a.x + best.dx * BIN_XY;
            double y = a.y + best.dy * BIN_XY;
            double theta = a.angle + Math.toRadians(best.dTheta * BIN_THETA);

            for (int i = 0; i < B.size(); i++) {
                if (used[i]) continue;

                Minutia b = B.get(i);

                double dist = Math.hypot(x - b.x, y - b.y);
                double angleDiff = Math.abs(theta - b.angle);

                if (dist < 20 && angleDiff < Math.toRadians(20)) {
                    matches++;
                    used[i] = true;
                    break;
                }
            }
        }
        //return (double) matches / Math.min(A.size(), B.size());
        return (double) matches / Math.sqrt(A.size()* B.size());
    }

    static List<RocPoint> computeROC(
            List<Double> genuineScores,
            List<Double> impostorScores,
            int steps) {

        List<RocPoint> roc = new ArrayList<>();

        for (int i = 0; i <= steps; i++) {
            double threshold = i / (double) steps;

            int falseAccept = 0;
            int falseReject = 0;

            for (double s : impostorScores)
                if (s >= threshold)
                    falseAccept++;

            for (double s : genuineScores)
                if (s < threshold)
                    falseReject++;

            double far = falseAccept / (double) impostorScores.size();
            double frr = falseReject / (double) genuineScores.size();

            roc.add(new RocPoint(threshold, far, frr));
        }
        return roc;
    }

    static double computeEER(List<RocPoint> roc) {
        double minDiff = Double.MAX_VALUE;
        double eer = 1.0;
        double eerThreshold = 0;

        for (RocPoint p : roc) {
            double diff = Math.abs(p.far - p.frr);
            if (diff < minDiff) {
                minDiff = diff;
                eer = (p.far + p.frr) / 2;
                eerThreshold = p.threshold;
            }
        }
        System.out.printf("Seuil EER = %.3f%n", eerThreshold);
        return eer;
    }


    static void saveROC(List<RocPoint> roc, String file) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println("threshold,FAR,FRR");

        for (RocPoint p : roc)
            pw.printf(Locale.US, "%.4f,%.6f,%.6f%n",
                    p.threshold, p.far, p.frr);

        pw.close();
    }

    static void identifyFromImage(
        String queryPath,
        String basePath,
        double threshold) throws Exception {

        // 1️⃣ Extraction de la requête
        int[][] img = normalize(loadGrayscaleImage(queryPath), 128, 128);
        double[][] ori = computeOrientation(img, 16);
        double[][] frequency2 = computeRidgeFrequency(
            img, ori, 16);
        img = gaborEnhance(img, ori, frequency2);
        int[][] bin = adaptiveBinarize(img, 16);
        int[][] thin = thinning(bin);

        List<Minutia> queryMin = filterMinutiaeStrong(
                extractMinutiae(thin, ori));

        //  Construction de la galerie (1 image par doigt)
        Map<String, List<Minutia>> gallery = new HashMap<>();

        for (int id = 101; id <= 110; id++) {
            String path = basePath + "\\" + id + "_1.tif";

            int[][] gimg = normalize(loadGrayscaleImage(path), 128, 128);
            double[][] gori = computeOrientation(gimg, 16);
            double[][] frequency3 = computeRidgeFrequency(
            gimg, gori, 16);
            gimg = gaborEnhance(gimg, gori, frequency3);
            int[][] gbin = adaptiveBinarize(gimg, 16);
            int[][] gthin = thinning(gbin);

            List<Minutia> gMin = filterMinutiaeStrong(
                    extractMinutiae(gthin, gori));

            gallery.put(String.valueOf(id), gMin);
        }

        //  Matching 1:N
        String bestId = null;
        double bestScore = 0;

        for (Map.Entry<String, List<Minutia>> e : gallery.entrySet()) {
            double score = matchMinutiaeHough(queryMin, e.getValue());
            System.out.println(score);

            if (score > bestScore) {
                bestScore = score;
                bestId = e.getKey();
            }
        }

        //  Décision
        System.out.println("========== IDENTIFICATION ==========");
        System.out.println("Meilleur ID : " + bestId);
        System.out.printf("Score : %.3f%n", bestScore);

        if (bestScore >= threshold)
            System.out.println("Décision : ✅ ACCEPTÉ");
        else
            System.out.println("Décision : ❌ REJETÉ");

    }

}
