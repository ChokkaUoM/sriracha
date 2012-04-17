package sriracha.frontend.android;

import android.content.Context;
import android.text.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.android.results.*;
import sriracha.frontend.model.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is the layout used to display the properties of a selected element.
 * When an element is selected, this class queries it for its properties, 
 * generates the appropriate views and displays them.
 * Since this class generates all the property controls, it is also responsible
 * for passing the values chosen for the properties to the circuit elements.
 */
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

        name.setFilters(new InputFilter[]{
                new InputFilter()
                {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
                    {
                        if (dstart == 0 && dest.length() > 0)
                        {
                            // Prevent deleting of the first character
                            return dest.charAt(0) + source.toString();
                        }
                        // Prevent non-alphanumeric characters
                        for (int i = start; i < end; i++)
                        {
                            if (!Character.isLetterOrDigit(source.charAt(i)))
                            {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
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

    public void showPropertiesFor(final CircuitElementView circuitElementView, final CircuitDesigner circuitDesigner)
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

                propertyValue.setText(scalarProperty.getValue());
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

                final Spinner source = (Spinner) referencePropertyView.findViewById(R.id.property_source);
                final View currentControlled = referencePropertyView.findViewById(R.id.current_controlled);
                final View voltageControlled = referencePropertyView.findViewById(R.id.voltage_controlled);
                final TextView sourceElement = (TextView) referencePropertyView.findViewById(R.id.source_element);
                final TextView sourceNode1 = (TextView) referencePropertyView.findViewById(R.id.source_node1);
                final TextView sourceNode2 = (TextView) referencePropertyView.findViewById(R.id.source_node2);

                propertiesView.addView(referencePropertyView);

                if (referenceProperty.getTypeId() == DependentSource.CURRENT_CONTROLLED)
                    showCurrentControlled(currentControlled, voltageControlled);
                else
                    showVoltageControlled(currentControlled, voltageControlled);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{
                        "Current Controlled",
                        "Voltage Controlled"
                });
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                source.setAdapter(adapter);
                source.setSelection(referenceProperty.getTypeId());
                source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
                    {
                        referenceProperty.setTypeId(position);
                        if (referenceProperty.getTypeId() == DependentSource.CURRENT_CONTROLLED)
                            showCurrentControlled(currentControlled, voltageControlled);
                        else
                            showVoltageControlled(currentControlled, voltageControlled);

                        CircuitElement element = circuitElementView.getElement();
                        EditText name = (EditText) findViewById(R.id.properties_name);
                        name.setText(element.getName());
                        circuitElementView.invalidate();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView)
                    {
                    }
                });

                sourceElement.setText(referenceProperty.getSourceElement());
                sourceElement.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ArrayList<CircuitElementView> elementViews = new ArrayList<CircuitElementView>();
                        ArrayList<CircuitElement> elements = referenceProperty.getSourceElementsList();
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
                                referenceProperty.setSourceElement(selectedElement.getElement().getName());
                            }
                        });
                        circuitDesigner.setCursorToSelectingElement(elementSelector);
                    }
                });

                sourceNode1.setText(referenceProperty.getSourceNode1());
                sourceNode2.setText(referenceProperty.getSourceNode2());

                View.OnClickListener listener = new OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        final WireManager wireManager = circuitDesigner.getWireManager();
                        final NodeCrawler crawler = new NodeCrawler(wireManager);
                        crawler.computeMappings();

                        final TextView textView = (TextView) view;
                        NodeSelector nodeSelector = new NodeSelector((TextView) view, wireManager.getSegments());
                        nodeSelector.setOnSelectListener(new IElementSelector.OnSelectListener<WireSegment>()
                        {
                            @Override
                            public void onSelect(WireSegment selectedSegment)
                            {
                                NetlistNode node = crawler.nodeFromSegment(selectedSegment);
                                if (node != null)
                                {
                                    textView.setText(node.toString());
                                    if (view == sourceNode1)
                                        referenceProperty.setSourceNode1(node.toString());
                                    else
                                        referenceProperty.setSourceNode2(node.toString());
                                }

                            }
                        });
                        circuitDesigner.setCursorToSelectingElement(nodeSelector);
                    }
                };
                sourceNode1.setOnClickListener(listener);
                sourceNode2.setOnClickListener(listener);
            }
        }
    }

    private void showCurrentControlled(View currentControlled, View voltageControlled)
    {
        voltageControlled.setVisibility(GONE);
        currentControlled.setVisibility(VISIBLE);
    }

    private void showVoltageControlled(View currentControlled, View voltageControlled)
    {
        voltageControlled.setVisibility(VISIBLE);
        currentControlled.setVisibility(GONE);
    }
}
