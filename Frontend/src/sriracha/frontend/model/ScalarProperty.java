package sriracha.frontend.model;

abstract public class ScalarProperty extends Property
{
    abstract public String getUnit();
    abstract public void setUnit(String unit);

    private String name;
    private String baseUnit;

    private static final String[] unitPrefixes = {"p", "n", "μ", "m", "", "k", "M", "G"};

    public ScalarProperty(String name, String baseUnit)
    {
        this.name = name;
        this.baseUnit = baseUnit;
    }

    public String getName() { return name; }
    public String getBaseUnit() { return baseUnit; }

    public String[] getUnitsList()
    {
        String[] units = new String[unitPrefixes.length];
        int i = 0;
        for (String prefix : unitPrefixes)
        {
            units[i++] = prefix + getBaseUnit();
        }
        return units;
    }
}
