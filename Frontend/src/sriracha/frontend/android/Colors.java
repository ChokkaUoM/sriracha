package sriracha.frontend.android;

import android.graphics.*;

import java.util.*;

public class Colors
{
    private static ArrayList<Integer> colors;
    private static ArrayList<Integer> desaturatedColors;

    public static void init()
    {
        colors = new ArrayList<Integer>();
        desaturatedColors = new ArrayList<Integer>();

        for (float hue = 0; hue < 360; hue += 60)
        {
            colors.add(Color.HSVToColor(new float[]{hue, 1, 1}));
        }
        for (float hue = 0; hue < 360; hue += 60)
        {
            desaturatedColors.add(Color.HSVToColor(new float[]{hue, 0.5f, 1}));
        }
    }

    public static int get(int index)
    {
        if (colors == null)
            init();

        index %= colors.size();
        return colors.get(index);
    }

    public static int getSecondary(int index)
    {
        if (desaturatedColors == null)
            init();

        index %= desaturatedColors.size();
        return desaturatedColors.get(index);
    }
}
