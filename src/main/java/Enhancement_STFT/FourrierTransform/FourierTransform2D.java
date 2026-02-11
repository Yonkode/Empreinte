package Enhancement_STFT.FourrierTransform;

public class FourrierTransform2D{

    public static complex [][] FT2D(int [][] signal2D)
    {
        int height = signal2D.length;
        int widht  = signal2D[0].length;
        complex FourrierTransform2D = complex [height][widht];

        for(int i= 0; i< height; i++)
        {
            for(int j=0; j<widht; j++)
            {
                for(int u=0; u<height; u++)
                {
                    for(int v=0; v<widht; v++)
                    {

                    }
                }
            }
        }
    }
}