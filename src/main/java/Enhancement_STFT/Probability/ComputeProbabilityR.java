package Enhancement_STFT.Probability;

public class ComputeProbabilityR {
    public static double [] probabilityOfR(double[][] probability)
    {
        int heigth = probability.length;
        int width = probability[0].length;
        double [] probaR = new double[heigth];

        for (int i=0; i<heigth; i++)
        {
            double sum = 0;
            for (int j=0; j<width; j++)
            {
                sum +=probability[i][j];
            }
            probaR[i] =sum;
        }
        return probaR;
    }
}
