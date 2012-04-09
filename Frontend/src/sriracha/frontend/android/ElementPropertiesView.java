package sriracha.frontend.android;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.R;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

import java.util.*;

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

    public void showPropertiesFor(CircuitElementView circuitElementView, final CircuitDesigner circuitDesigner)
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

                final TextView propertyName = (TextView) scalarPropertyView.findViewById(R.id.scalar_property_name);
                final EditText propertyValue = (EditText) scalarPropertyView.findViewById(R.id.scalar_property_value);
                final Spinner propertyUnits = (Spinner) scalarPropertyView.findViewById(R.id.scalar_property_unit_list);

                propertiesView.addView(scalarPropertyView);

                propertyName.setText(scalarProperty.getName());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, scalarProperty.getUnitsList());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                propertyUnits.setAdapter(adapter);
                propertyUnits.setSelection(Arrays.asList(scalarProperty.getUnitsList()).indexOf(scalarProperty.getUnit()));
                propertyUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View textView, int i, long l)
                    {
                        scalarProperty.setUnit(((TextView) textView).getText().toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView)
                    {
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

                final TextView value = (TextView) referencePropertyView.findViewById(R.id.property_value);

                propertiesView.addView(referencePropertyView);

                value.setText(referenceProperty.getValue());
                value.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ArrayList<CircuitElementView> elementViews = new ArrayList<CircuitElementView>();
                        ArrayList<CircuitElement> elements = referenceProperty.getElementsList();
                        for (CircuitElementView elementView : circuitDesigner.getElements())
                        {
                            if (elements.contains(elementView.getElement()))
                            {
                                elementViews.add(elementView);
                            }
                        }

                        ElementSelector elementSelector = new ElementSelector((TextView) view, elementViews);
                        elementSelector.setOnSelectListener(new ElementSelector.OnSelectListener<CircuitElementView>()
                        {
                            @Override
                            public void onSelect(CircuitElementView selectedElement)
                            {
                                referenceProperty._trySetValue(selectedElement.getElement().getName());
                            }
                        });
                        circuitDesigner.setCursorToSelectingElement(elementSelector);
                    }
                });
            }
        }
    }
}
