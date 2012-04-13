package sriracha.frontend;

import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.*;

import java.util.*;


/**
 * Generates the netlist representation of the current circuit and analysis requests
 */
public class NetlistGenerator
{
    private NodeCrawler crawler;
    private Collection<? extends CircuitElementView> elementViews;


    public NetlistGenerator(WireManager wireManager, Collection<? extends CircuitElementView> elementViews)
    {
        this.elementViews = elementViews;
        crawler = new NodeCrawler(wireManager);
    }


    public String generateNetlist()
    {
        crawler.computeMappings();

        String result = "Generated Netlist\n"; // First line is name of netlist

        for (CircuitElementView elementView : elementViews)
        {

            CircuitElementPortView[] ports = elementView.getPortViews();
            String[] nodeStrings = new String[ports.length];
            for(int i =0; i< ports.length; i++)
            {
                nodeStrings[i] = crawler.nodeFromIntersection(ports[i]).toString();
            }

            result += elementView.getElement().toNetlistString(nodeStrings, crawler) + "\n";
        }

        return result;
    }
}
