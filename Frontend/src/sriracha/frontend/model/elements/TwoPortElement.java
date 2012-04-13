package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

abstract public class TwoPortElement extends CircuitElement
{
    public static final int POSITIVE_NODE = 0;
    public static final int NEGATIVE_NODE = 1;

    public TwoPortElement(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    public int getPortCount() { return 2; }


}
