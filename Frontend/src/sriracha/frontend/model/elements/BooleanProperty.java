package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class BooleanProperty extends Property
{
    protected boolean value;
    private String name;

    public BooleanProperty(String name)
    {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String getValue()
    {
        return value ? "1" : "";
    }
    @Override
    public void trySetValue(String newValue)
    {
        value = !newValue.isEmpty();
    }
}
