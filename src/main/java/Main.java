import Enhancement_STFT.Windows.ComputeWindowOfSTFT;


import java.awt.image.BufferedImage;

public class Main {
    public static void main (String args[]) throws Exception {
        int [][] imgage = FingerprintMinutiae.loadGrayscaleImage("/home/odilon/Programming/Empreinte/contact-based_fingerprints/first_session/2_1.jpg");
        double[][] window = ComputeWindowOfSTFT.computeWindow();
        int [][] n= FingerprintMinutiae.normalize(imgage, 128, 128);


    }

    static void showGrayImage(double[][] img, String title) {

        int h = img.length;
        int w = img[0].length;

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        // Recherche min et max
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (img[y][x] < min) min = img[y][x];
                if (img[y][x] > max) max = img[y][x];
            }
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                double normalized = 255.0 * (img[y][x] - min) / (max - min);
                int v = (int) Math.round(normalized);

                int rgb = (v << 16) | (v << 8) | v;
                out.setRGB(x, y, rgb);
            }
        }

        javax.swing.JFrame frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new javax.swing.JLabel(new javax.swing.ImageIcon(out)));
        frame.pack();
        frame.setVisible(true);
    }

}
