package sriracha.frontend.android.designer;

import android.view.View;
import android.view.ViewGroup;
import sriracha.frontend.MainActivity;
import sriracha.frontend.R;
import sriracha.frontend.android.ElementPropertiesView;
import sriracha.frontend.android.model.CircuitElementView;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is the layout for the menu that chooses between the types of elements
 * to be added to the circuit designer.
 * Mostly is responsible for showing the menus that should be shown, and hiding
 * the menus that should be hidden.
 */
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

    public void showElementPropertiesMenu(CircuitElementView selectedElement, CircuitDesigner circuitDesigner)
    {
        showSubMenu(R.id.element_properties);

        if (selectedElement == null)
            return;

        ((ElementPropertiesView) getSelectedSubMenu()).showPropertiesFor(selectedElement, circuitDesigner);
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
