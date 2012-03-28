package sriracha.frontend.model.elements.ctlsources;

import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

public class DependentCurrentSource extends TwoPortElement
{
    @Override
    public Property[] getProperties()
    {
        return new Property[0];
    }

    @Override
    public String getType()
    {
        return "Dependent Current Source";
    }

    @Override
    public String getNameTemplate()
    {
        // VCCS - Symbol "G"
        // CCCS - Symbol "F"
        return "Id%d";
    }
}
