package sriracha.frontend.model.elements.ctlsources;

import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.model.*;

import java.io.*;

public class DependentVoltageSource extends DependentSource implements Serializable
{
    private String type = "Dependent Voltage Source";

    public DependentVoltageSource(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    @Override
    protected void rename()
    {
        if (typeId == VOLTAGE_CONTROLLED && name.equals("H" + index))
            name = "E" + index;
        else if (typeId == CURRENT_CONTROLLED && name.equals("E" + index))
            name = "H" + index;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getNameTemplate()
    {
        // VCVS - Symbol "E"
        // CCVS - Symbol "H"
        return "E%d";
    }
}
