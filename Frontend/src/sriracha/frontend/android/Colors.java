package sriracha.frontend.android;

import android.graphics.*;

import java.util.*;

public class Colors
{
    private static ArrayList<Integer> colors;

    public static void init()
    {
        colors = new ArrayList<Integer>();
        for (int i = 0; i < 2; i++)
        {
            float saturation = i == 0 ? 1 : 0.5f;
            for (float hue = 0; hue < 360; hue += 60)
            {
                colors.add(Color.HSVToColor(new float[]{hue, saturation, 1}));
            }
        }
    }

    public static int get(int index)
    {
        if (colors == null)
            init();

        index %= colors.size();
        return colors.get(index);
    }
}
