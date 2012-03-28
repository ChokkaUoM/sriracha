package sriracha.frontend.model;

abstract public class ReferenceProperty extends Property
{
    protected CircuitElement element;

    protected String getElementName() { return element != null ? element.getName() : "[Select...]"; }
    protected void setElement(String elementName)
    {
        // TODO: finish this
    }

    public String[] getElementsList()
    {
        return new String[0];
    }
}
