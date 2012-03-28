package sriracha.frontend.model;

abstract public class Property
{
    abstract public String getValue();

    abstract public void _trySetValue(String value);

    protected OnPropertyValueChangedListener onPropertyValueChangedListener;

    public void trySetValue(String value)
    {
        _trySetValue(value);
        if (onPropertyValueChangedListener != null)
            onPropertyValueChangedListener.onPropertyValueChanged(this);
    }

    public void setOnPropertyValueChangedListener(OnPropertyValueChangedListener onPropertyValueChangedListener)
    {
        this.onPropertyValueChangedListener = onPropertyValueChangedListener;
    }

    public interface OnPropertyValueChangedListener
    {
        public void onPropertyValueChanged(Property property);
    }
}
