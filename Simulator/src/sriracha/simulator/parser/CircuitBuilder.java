package sriracha.simulator.parser;

import sriracha.simulator.model.*;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class CircuitBuilder {


    public Circuit build(String netlist) {

        String[] lines = netlist.split("\\r?\\n");

        for (String line : lines)
            parseLine(line);

        return null;
    }

    private CircuitElement parseLine(String line) 
    {
        char elementType = Character.toLowerCase(line.charAt(0));
        
        String[] params = line.split("\\s+");

        if (params.length < 4)
            throw new ParseException("Not enough parameters for a circuit element: " + line);

        String[] additionalParams = new String[params.length - 3];
        for (int i = 3; i < params.length; i++)
            additionalParams[i - 3] = params[i];

        
        switch (elementType) {
            case 'r':
                return createResistor(params[1], params[2], params[3]);
            case 'i':
                return createCurrentSource(params[1], params[2], additionalParams);
           case 'v':
               return createVoltageSource(params[1], params[2], additionalParams);
            case 'l':
                return createInductor(params[1], params[2], params[3]);
            case 'c':
                return createCapacitor(params[1], params[2], params[3]);
        }
        
        throw new ParseException("Unrecognized element type: " + elementType);
    }

    private CurrentSource createCurrentSource(String node1, String node2, String... params) {
        return null;
    }

    private VoltageSource createVoltageSource(String node1, String node2, String... params) {
        return null;
    }

   private Resistor createResistor(String node1, String node2, String value) {
        return null; 
    }

    private Capacitor createCapacitor(String node1, String node2, String value) {
        return null;
    }

    private Inductor createInductor(String node1, String node2, String value) {
        return null;
    }
}
