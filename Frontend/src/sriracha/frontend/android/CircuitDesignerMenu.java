package sriracha.frontend.android;

import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;

import java.util.*;

public class CircuitDesignerMenu
{
    private MainActivity context;

    public CircuitDesignerMenu(MainActivity context)
    {
        this.context = context;
    }

    public void showSubMenu(int toShow)
    {
        for (View child : getAllSubMenus())
        {
            if (child.getId() == toShow)
                child.setVisibility(View.VISIBLE);
            else
                child.setVisibility(View.GONE);
        }
    }
    
    public void showElementPropertiesMenu(CircuitElementView selectedElement)
    {
        showSubMenu(R.id.element_properties);

        if (selectedElement == null)
            return;

        TextView type = (TextView)getSelectedSubMenu().findViewById(R.id.properties_type);
        TextView name = (TextView)getSelectedSubMenu().findViewById(R.id.properties_name);
        
        type.setText(selectedElement.getType());
        name.setText(String.format(selectedElement.getNameTemplate(), 1));
    }

    public ViewGroup getSelectedSubMenu()
    {
        for (View child : getAllSubMenus())
        {
            if (child.getVisibility() == View.VISIBLE)
                return (ViewGroup) child;
        }
        return null;
    }

    public void setSelectedItem(int itemId)
    {
        ViewGroup selectedSubMenu = getSelectedSubMenu();
        if (selectedSubMenu == null)
            return;

        for (View child : getAllSubViews(selectedSubMenu))
        {
            child.setSelected(child.getId() == itemId);
        }
    }

    private View[] getAllSubMenus()
    {
        ViewGroup root = (ViewGroup) context.findViewById(R.id.circuit_menu_container);
        View subMenus[] = new ViewGroup[root.getChildCount()];
        for (int i = 0; i < root.getChildCount(); i++)
            subMenus[i] = root.getChildAt(i);
        return subMenus;
    }

    private ArrayList<View> getAllSubViews(ViewGroup root)
    {
        ArrayList<View> views = new ArrayList<View>();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            View child = root.getChildAt(i);
            views.add(child);
            if (child instanceof ViewGroup)
                views.addAll(getAllSubViews((ViewGroup) child));
        }
        return views;
    }
}
