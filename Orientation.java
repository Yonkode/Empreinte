

public class Orientation {

    public static float [][] computeOrientationWithGradien(int [][] image, int block){
        int getHeight = image.length;
        int getWidth = image[0].length;
        int [][] Orientation = new int [getHeight][getWidth];

        for(int x = 0; x<getHeight-block; x+= block){
            for(int y = 0; y<getWidth-block; y+= block){
                float Gx= 0;
                float Gy= 0;
                for(int i = -block/2; i <= block/2 ; i++ ){
                    for(int j = -block / 2 ; j <= block/ 2 ; j++){
                        int dx = img[y + i][x + j + 1] - img[y + i][x + j - 1];
                        int dy = img[y + i + 1][x + j] - img[y + i - 1][x + j];

                        Gx += 2 * dx * dy;
                        Gy += dx * dx - dy * dy;
                    }
                }

                double theta = 0.5 * Math.atan2(gx, gy);

                for (int i = 0; i < block; i++)
                    for (int j = 0; j < block; j++)
                        if (y + i < h && x + j < w)
                            ori[y + i][x + j] = theta;
            }
        }
        return Orientation;
    }

    public static float [][] computeOrientationWithSlitMethod(int [][]image, int block){
        int getHeight = image.length;
        int getWidth = image[0].length;
        

    }
    public static float [][] computeOrientationInFrequencyDomain(int [][]image, int block){
        int getHeight = image.length;
        int getWidth = image[0].length;


    }
    
}
