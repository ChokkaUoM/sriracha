package sriracha.frontend.model;

abstract public class Property
{
    abstract public String getValue();
    abstract public void trySetValue(String value);
    abstract public String getUnit();
    abstract public void setUnit(String unit);
}
