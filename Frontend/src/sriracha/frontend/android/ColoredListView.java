package sriracha.frontend.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/04/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColoredListView extends ListView
{

    public ColoredListView(Context context)
    {
        super(context);
    }

    public ColoredListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ColoredListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    class ColoredStringAdapter extends ArrayAdapter<String>
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
}
