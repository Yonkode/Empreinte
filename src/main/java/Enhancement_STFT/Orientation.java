package Enhancement_STFT;

public class Orientation {
    double Energy(double [] probaTheta)
    {
        double energy = 0;
        int heigth = probaTheta.length;
        double x = 0, y = 0;
        double theta = 3.14;
        for (int i=0; i< heigth; i++)
        {
            y +=probaTheta[i]*Math.sin(2*theta);
            x +=probaTheta[i]*Math.cos(2*theta);
        }
        energy= (double) 1 /2 * Math.atan2(y,x);
        return energy;
    }
}
