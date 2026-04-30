package Enhancement_STFT.FourrierTransform;

public class Complex{
    
    double real;
    double imag;

    public Complex( double r, double i){
        this.real = r;
        this.imag = i;
    }
    public Complex(double r) {
        this.real = r;
        this.imag = 0;
    }

    public Complex add(Complex c) {
        return new Complex(this.real + c.real,
                this.imag + c.imag);
    }

    public Complex subtract(Complex c) {
        return new Complex(this.real - c.real,
                this.imag - c.imag);
    }

    public Complex multiply(Complex c) {
        double r = this.real * c.real - this.imag * c.imag;
        double i = this.real * c.imag + this.imag * c.real;
        return new Complex(r, i);
    }

    public Complex multiply(double c) {
        double r = this.real * c;
        double i = this.imag * c;
        return new Complex(r, i);
    }
    public double getReal()
    {
        return this.real;
    }
    public double getImg()
    {
        return this.imag;
    }
    public static  double magnitude(double real,double img)
    {
        double abs = Math.sqrt(Math.pow(real,2) + Math.pow(img,2));
        return abs;
    }
    public static double phase(double real, double imag) {
        return Math.atan2(imag, real);
    }

    public double magnitude() {
        return Math.sqrt(this.real * this.real + this.imag * this.imag);
    }
    public double phase() {
        return Math.atan2(this.imag, this.real);
    }

    public static double[][] magnitude2D(Complex[][] fourierTransform)
    {
        int height = fourierTransform.length;
        int width = fourierTransform[0].length;
        double [][] out = new double[height][width];

        for (int u = 0 ; u < height; u++)
        {
            for (int v = 0 ; v < width; v++)
            {
                out[u][v] = fourierTransform[u][v].magnitude();
            }
        }
        return out;
    }
}
