package sriracha.frontend.model.elements.ctlsources;

import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.sources.*;

import java.io.*;
import java.util.*;

public class DependentCurrentSource extends DependentSource implements Serializable
{
    private String type = "Dependent Current Source";

    public DependentCurrentSource(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    @Override
    protected void rename()
    {
        if (typeId == VOLTAGE_CONTROLLED && name.equals("F"+index))
            name = "G" + index;
        else if (typeId == CURRENT_CONTROLLED && name.equals("G"+index))
            name = "F" + index;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getNameTemplate()
    {
        // VCCS - Symbol "G"
        // CCCS - Symbol "F"
        return "G%d";
    }
}
