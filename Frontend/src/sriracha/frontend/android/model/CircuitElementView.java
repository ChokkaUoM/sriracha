package sriracha.frontend.android.model;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import sriracha.frontend.model.*;
import sriracha.frontend.util.*;

public abstract class CircuitElementView extends View
{
    private CircuitElement element;

    protected Bitmap bitmap;

    protected Vector2 position;
    private final Paint paint = new Paint();

    public abstract int getDrawableId();

    public CircuitElementView(Context context, CircuitElement element, Vector2 position)
    {
        super(context);
        this.element = element;
        this.position = position;
        bitmap = getBitmapFromDrawable();
    }

    private Bitmap getBitmapFromDrawable()
    {
        Drawable drawable = getContext().getResources().getDrawable(getDrawableId());
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, position.getX(), position.getY(), paint);
    }
}
