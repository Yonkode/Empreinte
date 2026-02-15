package Enhancement_STFT;

public class Filtering {
    public static double[][] computeFRBlock(double[][] frequency, double sigmaR) {
        int height = frequency.length;
        int width = frequency[0].length;
        double[][] fr = new double[height][width];

        int u_c = width / 2;
        int v_c = height / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = Math.sqrt((x - u_c) * (x - u_c) + (y - v_c) * (y - v_c));
                fr[y][x] = Math.exp(-Math.pow(r - frequency[y][x], 2) / (2 * sigmaR * sigmaR));
            }
        }
        return fr;
    }


    public static double[][] computeFABlock(double[][] orientation, double[][] coherence) {
        int height = orientation.length;
        int width = orientation[0].length;
        double[][] fa = new double[height][width];

        int u_c = width / 2;
        int v_c = height / 2;
        double k = 0.5; // paramètre ajustable pour sigmaTheta
        double epsilon = 0.01;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sigmaTheta = k / (coherence[y][x] + epsilon);
                double du = x - u_c;
                double dv = y - v_c;
                double thetaUV = Math.atan2(dv, du);

                // différence angulaire modulée dans [-pi/2, pi/2]
                double dTheta = thetaUV - orientation[y][x];
                dTheta = (dTheta + Math.PI) % Math.PI - Math.PI / 2;

                fa[y][x] = Math.exp(-(dTheta * dTheta) / (2 * sigmaTheta * sigmaTheta));
            }
        }

        return fa;
    }

    public static double[][] computeFR(double[][] frequency, int block, double sigmaR) {
        int height = frequency.length;
        int width = frequency[0].length;
        double[][] fr = new double[height][width];

        // Parcours bloc par bloc
        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {
                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);
                int uc = bw / 2;
                int vc = bh / 2;

                // fréquence moyenne du bloc
                double freqBlock = 0;
                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        freqBlock += frequency[y + i][x + j];
                    }
                }
                freqBlock /= (bh * bw);

                // Calcul du filtre radial pour ce bloc
                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        double r = Math.sqrt((j - uc) * (j - uc) + (i - vc) * (i - vc));
                        fr[y + i][x + j] = Math.exp(-Math.pow(r - freqBlock, 2) / (2 * sigmaR * sigmaR));
                    }
                }
            }
        }
        return fr;
    }

    public static double[][] computeFA(double[][] orientation, double[][] coherence, int block) {
        int height = orientation.length;
        int width = orientation[0].length;
        double[][] fa = new double[height][width];
        double k = 0.5;
        double epsilon = 0.01;

        // Parcours bloc par bloc
        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {
                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);
                int uc = bw / 2;
                int vc = bh / 2;

                // orientation et cohérence moyennes du bloc
                double oriBlock = 0;
                double cohBlock = 0;
                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        oriBlock += orientation[y + i][x + j];
                        cohBlock += coherence[y + i][x + j];
                    }
                }
                oriBlock /= (bh * bw);
                cohBlock /= (bh * bw);

                double sigmaTheta = k / (cohBlock + epsilon);

                // Calcul du filtre angulaire pour le bloc
                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        double du = j - uc;
                        double dv = i - vc;
                        double thetaUV = Math.atan2(dv, du);
                        double dTheta = thetaUV - oriBlock;
                        dTheta = (dTheta + Math.PI) % Math.PI - Math.PI / 2;
                        fa[y + i][x + j] = Math.exp(-(dTheta * dTheta) / (2 * sigmaTheta * sigmaTheta));
                    }
                }
            }
        }
        return fa;
    }
}
