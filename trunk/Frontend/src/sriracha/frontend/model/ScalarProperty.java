package sriracha.frontend.model;

import java.util.*;

abstract public class ScalarProperty extends Property
{
    abstract public String getUnit();

    abstract public void setUnit(String unit);

    private String name;
    private String baseUnit;

    private static final String[] unitPrefixes = {"f", "p", "n", "μ", "m", "", "k", "M", "G", "T"};
    private static final String[] spicePrefixes = {"f", "p", "n", "u", "m", "", "k", "Meg", "G", "T"};

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

    public static String translateUnit(String unit)
    {
        String prefix = "";
        if (unit != null && !unit.isEmpty())
            prefix = unit.substring(0, Math.max(0, unit.length() - 1));

        int index = Arrays.asList(unitPrefixes).indexOf(prefix);
        if (index != -1)
            return spicePrefixes[index];
        throw new RuntimeException("Invalid unit: " + unit);
    }
}