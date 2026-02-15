package Enhancement_STFT;

public class RegionMask {
    public int[][] computeMask(double[][] coherence, double threshold)
    {
        int height = coherence.length;
        int width = coherence[0].length;
        int[][] mask = new int[height][width];

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                mask[i][j] = (coherence[i][j] >= threshold) ? 1 : 0;
            }
        }
        return mask;
    }

}
