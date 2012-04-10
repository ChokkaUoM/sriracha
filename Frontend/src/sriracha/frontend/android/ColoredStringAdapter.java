package sriracha.frontend.android;

import android.content.*;
import android.view.*;
import android.widget.*;

import java.util.*;

public class ColoredStringAdapter extends ArrayAdapter<String>
{

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextColor(Colors.get(position));
        return view;
    }

    public ColoredStringAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ColoredStringAdapter(Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
    }

    public ColoredStringAdapter(Context context, int textViewResourceId, String[] objects)
    {
        super(context, textViewResourceId, objects);
    }

    public ColoredStringAdapter(Context context, int resource, int textViewResourceId, String[] objects)
    {
        super(context, resource, textViewResourceId, objects);
    }

    public ColoredStringAdapter(Context context, int textViewResourceId, List<String> objects)
    {
        super(context, textViewResourceId, objects);
    }

    public ColoredStringAdapter(Context context, int resource, int textViewResourceId, List<String> objects)
    {
        super(context, resource, textViewResourceId, objects);
    }
}
