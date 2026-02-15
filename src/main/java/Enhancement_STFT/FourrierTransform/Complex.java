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


}
