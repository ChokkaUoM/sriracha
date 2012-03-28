package sriracha.frontend.android;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.R;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

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

    private void showNameAndType(final CircuitElementView circuitElementView)
    {
        final CircuitElement element = circuitElementView.getElement();
        final CircuitElementManager elementManager = element.getElementManager();

        final TextView type = (TextView) findViewById(R.id.properties_type);
        final EditText name = (EditText) findViewById(R.id.properties_name);

        type.setText(element.getType());
        name.setText(element.getName());

        name.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                String newName = textView.getText().toString();
                CircuitElement sameNameElement = elementManager.getElementByName(newName);

                if (sameNameElement == null || sameNameElement == element)
                {
                    element.setName(newName);
                    circuitElementView.invalidate();
                }
                else
                {
                    name.setText(element.getName());
                    Toast toast = Toast.makeText(getContext(), "The name \"" + newName + "\" is already in use.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                return true;
            }
        });
    }

    public void showPropertiesFor(CircuitElementView circuitElementView)
    {
        showNameAndType(circuitElementView);

        ViewGroup propertiesView = (ViewGroup) findViewById(R.id.properties_current_property);
        propertiesView.removeAllViews();

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

                propertiesView.addView(scalarPropertyView);

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
                            propertyValue.setText(scalarProperty.getValue());
                            Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        return true;
                    }
                });
            }
            else if (property instanceof ReferenceProperty)
            {
                final ReferenceProperty referenceProperty = (ReferenceProperty) property;
                final View referencePropertyView = LayoutInflater.from(getContext())
                        .inflate(R.layout.element_reference_property, this, false);

                final TextView propertyValue = (TextView) referencePropertyView.findViewById(R.id.property_value);
                final ListView valueListView = (ListView) referencePropertyView.findViewById(R.id.property_value_list);

                propertiesView.addView(referencePropertyView);

                propertyValue.setText(referenceProperty.getValue());

                valueListView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, referenceProperty.getElementsList()));
                valueListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View textView, int i, long l)
                    {
                        adapterView.setVisibility(GONE);
                        referenceProperty._trySetValue(((TextView) textView).getText().toString());
                        propertyValue.setText(referenceProperty.getValue());
                    }
                });

                propertyValue.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        valueListView.setVisibility(VISIBLE);
                    }
                });
            }
        }
    }
}
