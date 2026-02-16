package Enhancement_STFT;

import Enhancement_STFT.FourrierTransform.Complex;

public class Filtering {
    public static Complex[][] applyFilter(Complex[][] fftBlock, double[][] fr, double[][] fa) {
        int height = fftBlock.length;
        int width = fftBlock[0].length;

        Complex[][] filtered = new Complex[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double magnitude = fftBlock[i][j].magnitude() * fr[i][j] * fa[i][j];
                double phase = fftBlock[i][j].phase();

                // Reconstruire le nombre complexe filtré
                filtered[i][j] = new Complex(magnitude * Math.cos(phase),
                        magnitude * Math.sin(phase));
            }
        }

        return filtered;
    }

    public static double[][] computeFRBlock(Complex[][] fftBlock, double sigmaR) {
        int height = fftBlock.length;
        int width = fftBlock[0].length;
        double[][] fr = new double[height][width];

                // Calcul du filtre radial pour ce bloc
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        double r = fftBlock[i][j].magnitude() -fftBlock[height/2][width/2].magnitude();
                        fr[i][j] = Math.exp(-Math.pow(r , 2) / (2 * sigmaR * sigmaR));
                    }
                }
        return fr;
    }

    public static double[][] computeFABlock(Complex[][] fftBlock, double sigmaTheta) {
        int height = fftBlock.length;
        int width = fftBlock[0].length;
        double[][] fa = new double[height][width];
                // Calcul du filtre angulaire pour le bloc
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < height; j++) {

                        double dTheta = fftBlock[i][j].phase() -fftBlock[height/2][width/2].phase();
                        fa[i][j] = Math.exp(-(dTheta * dTheta) / (2 * sigmaTheta * sigmaTheta));
                    }
                }
        return fa;
    }

    public static double[][] computeFRButterworthBlock(
            double[][] frequency,
            int block,
            double bandwidth,
            int order) {

        int height = frequency.length;
        int width = frequency[0].length;
        double[][] fr = new double[height][width];

        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {

                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);

                int uc = bw / 2;
                int vc = bh / 2;

                // fréquence moyenne du bloc
                double rho0 = 0;
                for (int i = 0; i < bh; i++)
                    for (int j = 0; j < bw; j++)
                        rho0 += frequency[y + i][x + j];

                rho0 /= (bh * bw);

                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {

                        double du = j - uc;
                        double dv = i - vc;
                        double rho = Math.sqrt(du * du + dv * dv);

                        if (rho == 0) {
                            fr[y + i][x + j] = 0;
                            continue;
                        }

                        double term = (rho * rho - rho0 * rho0) /
                                (rho * bandwidth);

                        fr[y + i][x + j] =
                                1.0 / (1.0 + Math.pow(term, 2 * order));
                    }
                }
            }
        }

        return fr;
    }

    public static double[][] computeFAButterworthBlock(
            double[][] orientation,
            double[][] coherence,
            int block,
            double thetaBW,
            int order) {

        int height = orientation.length;
        int width = orientation[0].length;
        double[][] fa = new double[height][width];

        double epsilon = 0.01;

        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {

                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);

                int uc = bw / 2;
                int vc = bh / 2;

                // orientation et cohérence moyennes
                double theta0 = 0;
                double coh = 0;

                for (int i = 0; i < bh; i++)
                    for (int j = 0; j < bw; j++) {
                        theta0 += orientation[y + i][x + j];
                        coh += coherence[y + i][x + j];
                    }

                theta0 /= (bh * bw);
                coh /= (bh * bw);

                // largeur angulaire adaptative
                double effectiveBW = thetaBW / (coh + epsilon);

                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {

                        double du = j - uc;
                        double dv = i - vc;
                        double theta = Math.atan2(dv, du);

                        double dTheta = theta - theta0;
                        dTheta = (dTheta + Math.PI) % Math.PI - Math.PI / 2;

                        double ratio = dTheta / effectiveBW;

                        fa[y + i][x + j] =
                                1.0 / (1.0 + Math.pow(ratio, 2 * order));
                    }
                }
            }
        }

        return fa;
    }

    public static double[][] computeFRButterworthFFTEnhance(
            double[][] frequency, // carte de fréquence locale
            int block,            // taille du bloc
            double bandwidth,     // ρ_BW
            int order) {          // n
        int height = frequency.length;
        int width = frequency[0].length;
        double[][] fr = new double[height][width];

        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {

                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);
                int uc = bw / 2;
                int vc = bh / 2;

                // fréquence moyenne du bloc
                double rho0 = 0;
                for (int i = 0; i < bh; i++)
                    for (int j = 0; j < bw; j++)
                        rho0 += frequency[y + i][x + j];
                rho0 /= (bh * bw);

                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        double du = j - uc;
                        double dv = i - vc;
                        double rho = Math.sqrt(du * du + dv * dv);

                        double numerator = Math.pow(rho * bandwidth, 2 * order);
                        double denominator = numerator + Math.pow(rho * rho - rho0 * rho0, 2 * order);

                        fr[y + i][x + j] = denominator != 0 ? numerator / denominator : 0;
                    }
                }
            }
        }

        return fr;
    }

    public static double[][] computeFAButterworthFFTEnhance(
            double[][] orientation, // carte d’orientation locale
            double[][] coherence,  // carte de cohérence
            int block,              // taille du bloc
            double phiBW) {         // support angulaire de base (π/4 typiquement)
        int height = orientation.length;
        int width = orientation[0].length;
        double[][] fa = new double[height][width];

        double epsilon = 0.01;

        for (int y = 0; y < height; y += block) {
            for (int x = 0; x < width; x += block) {

                int bh = Math.min(block, height - y);
                int bw = Math.min(block, width - x);
                int uc = bw / 2;
                int vc = bh / 2;

                // orientation et cohérence moyennes du bloc
                double thetaC = 0;
                double cohBlock = 0;
                for (int i = 0; i < bh; i++)
                    for (int j = 0; j < bw; j++) {
                        thetaC += orientation[y + i][x + j];
                        cohBlock += coherence[y + i][x + j];
                    }
                thetaC /= (bh * bw);
                cohBlock /= (bh * bw);

                // largeur angulaire adaptative
                double effectiveBW = phiBW / (cohBlock + epsilon);

                for (int i = 0; i < bh; i++) {
                    for (int j = 0; j < bw; j++) {
                        double du = j - uc;
                        double dv = i - vc;
                        double phi = Math.atan2(dv, du);

                        double delta = phi - thetaC;
                        delta = (delta + Math.PI) % Math.PI - Math.PI / 2;

                        if (Math.abs(delta) < effectiveBW) {
                            fa[y + i][x + j] = Math.pow(Math.cos(Math.PI * delta / (2 * effectiveBW)), 2);
                        } else {
                            fa[y + i][x + j] = 0;
                        }
                    }
                }
            }
        }

        return fa;
    }

}
