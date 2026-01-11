import java.awt.image.BufferedImage;
public class Main {
    public static void main(String[] args) throws Exception {

        int[][] matrice = Image.imageToMatrice("C:\\Programmation\\Java\\Java\\Empreinte\\fingerprints\\DB1_B\\101_1.tif");
        float mean = Normalisation.average(matrice);
float var  = Normalisation.variance(matrice, mean);

        int [][] G = Normalisation.normalize(matrice, mean, var);
        BufferedImage img = Image.grayToImage(G);

        Image.show(img, "Image Grayscale");
    }
}
