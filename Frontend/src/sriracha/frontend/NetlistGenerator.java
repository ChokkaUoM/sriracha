package sriracha.frontend;

import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.CircuitElementManager;
import sriracha.frontend.model.CircuitElementPort;
import sriracha.frontend.model.elements.sources.Ground;

import java.util.*;


/**
 * Generates the netlist representation of the current circuit and analysis requests
 * Note: this could be made more efficient if needed, because we traverse the graph
 * in generate(), but also in mapSegmentToNode().
 */
public class NetlistGenerator
{
    public String generate(WireManager wireManager, CircuitElementManager circuitElementManager)
    {
        NodeCrawler crawler = new NodeCrawler();
        ArrayList<NetlistNode> nodes = crawler.getNodeList(wireManager);
        String result = "Generated Netlist\n"; // First line is name of netlist

        for (CircuitElement element : circuitElementManager.getElements())
        {
            int[] indices = crawler.getNodeIndices(nodes, element);

            if (indices.length == 2)
            {
                NetlistNode node1 = nodes.get(indices[0]);
                NetlistNode node2 = nodes.get(indices[1]);

                CircuitElementPort[] ports = element.getPorts();
                if (node2.containsPort(ports[0]))
                {
                    // swap node1 and node2
                    NetlistNode temp = node1;
                    nodes.set(0, node2);
                    nodes.set(1, temp);
                }
            }

            String[] nodeStrings = new String[indices.length];
            for (int i = 0; i < indices.length; i++)
                nodeStrings[i] = nodes.get(indices[i]).toString();

            result += element.toNetlistString(nodeStrings) + "\n";
        }

        return result;
    }
}
