package sriracha.frontend.model.elements.sources;

import sriracha.frontend.*;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.CircuitElementManager;
import sriracha.frontend.model.Property;

public class Ground extends CircuitElement {
    public Ground(CircuitElementManager elementManager) {
        super(elementManager);
    }

    @Override
    public Property[] getProperties() {
        return new Property[0];
    }

    @Override
    public int getPortCount() {
        return 1;
    }

    @Override
    public String getType() {
        return "Ground";
    }

    @Override
    public String getNameTemplate() {
        return "G%d";
    }

    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler) {
        return "";
    }
}
