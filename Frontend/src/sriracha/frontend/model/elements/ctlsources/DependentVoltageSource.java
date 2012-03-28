package sriracha.frontend.model.elements.ctlsources;

import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

public class DependentVoltageSource extends TwoPortElement
{
    private Property[] properties;

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            properties = new Property[]{
                    new ReferenceProperty()
                    {
                        @Override
                        public String getValue()
                        {
                            return getElementName();
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            setElement(value);
                        }
                    }
            };
        }
        return properties;
    }

    @Override
    public String getType()
    {
        return "Dependent Voltage Source";
    }

    @Override
    public String getNameTemplate()
    {
        // VCVS - Symbol "E"
        // CCVS - Symbol "H"
        return "Vd%d";
    }
}
