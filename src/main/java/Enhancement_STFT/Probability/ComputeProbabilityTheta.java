package Enhancement_STFT.Probability;

public class ComputeProbabilityTheta {
    public static double [] probabilityOfTheta(double[][] probability)
    {
        int heigth = probability.length;
        int width = probability[0].length;
        double [] probaTheta = new double[heigth];

        for (int i=0; i<heigth; i++)
        {
            double sum = 0;
            for (int j=0; j<width; j++)
            {
                sum +=probability[j][i];
            }
            probaTheta[i] =sum;
        }
        return probaTheta;
    }
}
