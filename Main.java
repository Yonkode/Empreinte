import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.commons.math4;

public class Main{
    public static void main (String[] args) throws IOException {
        String path= "finger.BMP";
        BufferedImage IMG = ImageIO.read(new File(path));
        int[][] mat = imageConvertToMatrix(IMG);
        
        for(int [] i : mat){
            for(int j : i){
                System.out.print(j + " ");
            }
            System.out.println("");
        }
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

    public static float meanCalcul(int[][] matrix, int height, int width){
        float mean = 0;

        for(int [] i : matrix ){
            for(int j : i){
                mean += j;
            } 
        }
        mean = mean * 1/height*width;
        return mean;
    }

    public static float varianceCalcul(int[][] matrix, int height, int width, float mean){
        float var = 0;

        for(int [] i : matrix ){
            for(int j : i){
                var += Math.pow(j - mean,2);
            } 
        }

        var = var * 1/height*width;

        return var;
    }

    public static float [][] normalizationOfImage(int [][] matrix ,int height, int width , float mean, float var){
         final float m0 = 1;
         final float var0 = 1;
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
                         Vy += Math.pow(dx,2)*Math.pow(dy,2);
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

    public static float [][] ridgeFrequencyImage(int [][] O,int [][] G, int height, int width){
        float [][] omega = new float[height][width];
        float [][] omegaPrime = new float[height][width];

        return omegaPrime;
    }

}