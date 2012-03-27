package sriracha.frontend.android;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.R;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

public class ElementPropertiesView extends LinearLayout
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
                final View scalarPropertyView = LayoutInflater.from(getContext())
                        .inflate(R.layout.element_scalar_property, this, false);
                final TextView propertyName = (TextView) scalarPropertyView.findViewById(R.id.property_name);
                final TextView propertyUnit = (TextView) scalarPropertyView.findViewById(R.id.property_unit);
                final ListView unitsListView = (ListView) scalarPropertyView.findViewById(R.id.property_unit_list);
                final EditText propertyValue = (EditText) scalarPropertyView.findViewById(R.id.property_value);

                addView(scalarPropertyView);

                propertyName.setText(scalarProperty.getName());
                propertyUnit.setText(scalarProperty.getUnit());

                unitsListView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, scalarProperty.getUnitsList()));
                unitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View textView, int i, long l)
                    {
                        adapterView.setVisibility(GONE);
                        scalarProperty.setUnit(((TextView) textView).getText().toString());
                        propertyUnit.setText(scalarProperty.getUnit());
                    }
                });

                propertyUnit.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        unitsListView.setVisibility(VISIBLE);
                    }
                });

                propertyValue.setText(property.getValue());
                propertyValue.setOnEditorActionListener(new TextView.OnEditorActionListener()
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
            else if (property instanceof BooleanProperty)
            {
                final BooleanProperty booleanProperty = (BooleanProperty) property;
                final View booleanPropertyView = LayoutInflater.from(getContext())
                        .inflate(R.layout.element_boolean_property, this, false);
                final CheckBox propertyValue = (CheckBox) booleanPropertyView.findViewById(R.id.property_value);

                addView(booleanPropertyView);

                propertyValue.setText(booleanProperty.getName());
                propertyValue.setChecked(!booleanProperty.getValue().isEmpty());
            }
        }
    }
}
