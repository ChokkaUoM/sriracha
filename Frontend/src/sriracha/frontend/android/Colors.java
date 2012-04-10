package sriracha.frontend.android;

import android.graphics.*;

public class Colors
{
    private final static int NUM_COLORS = 27;
    private static int[] colors;

    public static void init()
    {
        colors = new int[NUM_COLORS];
        int rs = 0, gs = 0, bs = 0;
        for (int i = 0; i < NUM_COLORS; i++)
        {
            int r, g, b;
            r = rs++;
            if (r > 1)
            {
                rs = 0;
                gs++;
            }
            g = gs;
            if (gs > 1)
            {
                gs = 0;
                bs++;
            }
            b = bs;

            colors[i] = Color.rgb(0x7F * r, 0x7F * g, 0x7F * b);
        }
    }

    public static int get(int index)
    {
        if (colors == null)
            init();
        return colors[Math.max(0, Math.max(colors.length - 1, index))];
    }
}
