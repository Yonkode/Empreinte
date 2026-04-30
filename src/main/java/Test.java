import Enhancement_STFT.*;
import Enhancement_STFT.FourrierTransform.Complex;

import java.awt.image.BufferedImage;

import static Enhancement_STFT.FourrierTransform.Complex.magnitude2D;
import static Enhancement_STFT.FourrierTransform.FourierTransform.ComputeFourierTransform2D;
import static Enhancement_STFT.FourrierTransform.InverseFourierTransform.ComputeInverseFourierTransform2D;

public class Test {
    public static void main(String[] args) throws Exception {
        int [][] imgage =image.loadGrayscaleImage("/home/odilon/Programming/Empreinte/contact-based_fingerprints/first_session/1_1.jpg");
        double[][] img = convertIntInDouble(imgage);
            showGrayImage(img, "img");

        Complex [][] imgTransformFourier = ComputeFourierTransform2D(img);
        System.out.println("FT TERMINATE");

        double [][] magnitude = magnitude2D(imgTransformFourier);
        showGrayImage(magnitude, "Magnitude");

        System.out.println("Magnitude TERMINATE");
        double [][] inverseimgTransformFourier = ComputeInverseFourierTransform2D(imgTransformFourier);

        System.out.println("IFT TERMINATE");
        showGrayImage(inverseimgTransformFourier, "img fourier resulte");
    }

    static double[][] convertIntInDouble(int[][] img){
        int h = img.length, w = img[0].length;
        double out[][] = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                out[y][x] = img[y][x];
            }
        }
        return out;
    }

    static void showGrayImage(double[][] img, String title) {

        int h = img.length;
        int w = img[0].length;

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                double normalized = 255.0 * ( - min) / (max - min);
                int v = (int) img[y][x];

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
