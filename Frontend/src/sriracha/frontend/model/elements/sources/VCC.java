package sriracha.frontend.model.elements.sources;

import sriracha.frontend.*;
import sriracha.frontend.model.*;

public class VCC extends CircuitElement
{
    public VCC(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    @Override
    public Property[] getProperties()
    {
        return new Property[0];
    }

    @Override
    public int getPortCount() { return 1; }

    @Override
    public String getType()
    {
        return "VCC";
    }

    @Override
    public String getNameTemplate()
    {
        return "VCC%d";
    }
    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        return "";
    }
}
