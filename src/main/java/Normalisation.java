public class Normalisation {
    public static float average(int[][] img){
        int h = img.length, w = img[0].length;
        int imgSize = h*w;
        float mean = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                mean += img[y][x];
            }
        }
        mean /= imgSize;
        return mean;
    }

    public static float variance(int[][] img, float mean){
        int h = img.length, w = img[0].length;
        int imgSize = h*w;
        float var = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                var += (img[y][x] - mean) * (img[y][x] - mean);
            }
        }
        var /= imgSize;
        return var;
    }
    
    public static int[][] normalize(int[][] img, float mean, float var) {
        int h = img.length, w = img[0].length;
        float Mo = 100; float V0 = 100;
        int normalizeImage[][] = new int [h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (img[y][x] > mean) {
                    normalizeImage[y][x] = (int)(Mo + Math.sqrt((img[y][x] - mean) * (img[y][x] - mean) * V0 / var));
                } else {
                    normalizeImage[y][x] = (int)(Mo - Math.sqrt((img[y][x] - mean) * (img[y][x] - mean) * V0 / var));
                }
            }
        }
        
        return normalizeImage;

    }
}
