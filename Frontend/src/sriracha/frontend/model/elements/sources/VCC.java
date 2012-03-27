package sriracha.frontend.model.elements.sources;

import sriracha.frontend.model.*;

public class VCC extends CircuitElement
{
    @Override
    public Property[] getProperties()
    {
        return new Property[0];
    }
    @Override
    public int getPortCount() { return 1; }
}
