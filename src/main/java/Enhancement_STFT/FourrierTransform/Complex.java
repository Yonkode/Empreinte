package Enhancement_STFT.FourrierTransform;

public class Complex{
    
    float real;
    float img;

    Complex( float r, float i){
        this.real = r;
        this.img = i;
    }

    public static  float module{
        float abs = Math.sqrt(Math.pow(this.real,2)+Math.pow(this.img,2));
        return abs;
    }
    
}
