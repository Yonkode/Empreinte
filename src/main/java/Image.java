import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
public class Image {
    public static int [][] imageToMatrice(String path) throws Exception{
        BufferedImage img = ImageIO.read(new File(path));
        int w = img.getWidth(), h = img.getHeight();
        int[][] gray = new int[h][w];

        Raster raster = img.getRaster();
        int bands = raster.getNumBands();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (bands == 1) {
                    // vraie image grayscale
                    gray[y][x] = raster.getSample(x, y, 0);
                } else {
                    // image couleur → conversion perceptuelle
                    int r = raster.getSample(x, y, 0);
                    int g = raster.getSample(x, y, 1);
                    int b = raster.getSample(x, y, 2);
                    gray[y][x] = (77*r + 150*g + 29*b) >> 8;
                }
            }
        }

        return gray;

    }

    public static BufferedImage grayToImage(int[][] gray) {
        int h = gray.length;
        int w = gray[0].length;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = gray[y][x];

                // sécurité
                if (v < 0) v = 0;
                if (v > 255) v = 255;

                raster.setSample(x, y, 0, v);
            }
        }
        return img;
    }

    public static void show(BufferedImage img, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        };

        panel.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
