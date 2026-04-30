package Enhancement_STFT;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class image {
    public static int[][] loadGrayscaleImage(String path) throws Exception {
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

    }
