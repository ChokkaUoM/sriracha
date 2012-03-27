package sriracha.frontend.android;

import android.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.R;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class ElementPropertiesView extends RelativeLayout
{
    public ElementPropertiesView(Context context)
    {
        super(context);
    }
    public ElementPropertiesView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public ElementPropertiesView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void showPropertiesFor(CircuitElementView circuitElementView)
    {
        removeAllViews();
        for (Property property : circuitElementView.getElement().getProperties())
        {
            if (property instanceof ScalarProperty)
            {
                final ScalarProperty scalarProperty = (ScalarProperty) property;
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.element_scalar_property, this, true);
                ((TextView) view.findViewById(R.id.property_name)).setText(scalarProperty.getName());
                ((TextView) view.findViewById(R.id.property_unit)).setText(scalarProperty.getUnit());

                final ListView unitContainer = (ListView) view.findViewById(R.id.property_unit_list);
                unitContainer.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, scalarProperty.getUnitsList()));
                unitContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View textView, int i, long l)
                    {
                        adapterView.setVisibility(GONE);
                        scalarProperty.setUnit(((TextView)textView).getText().toString());
                        ((TextView) view.findViewById(R.id.property_unit)).setText(scalarProperty.getUnit());
                    }
                });

                view.findViewById(R.id.property_unit).setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        unitContainer.setVisibility(VISIBLE);
                    }
                });

                EditText input = (EditText) view.findViewById(R.id.property_value);
                input.setText(property.getValue());
                input.setOnEditorActionListener(new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
                    {
                        try
                        {
                            scalarProperty.trySetValue(textView.getText().toString());
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
            }
        }

    }
}
