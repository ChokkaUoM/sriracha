package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;
import sriracha.frontend.util.*;

public class CurrentSourceView extends CircuitElementView
{
    public CurrentSourceView(Context context, CircuitElement element, Vector2 position)
    {
        super(context, element, position);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_current;
    }
}
