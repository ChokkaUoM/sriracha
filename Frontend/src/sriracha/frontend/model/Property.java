package sriracha.frontend.model;

abstract public class Property
{
    abstract public String getValue();

    abstract public void trySetValue(String value);

    private boolean isEnabled = true;

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }
}
