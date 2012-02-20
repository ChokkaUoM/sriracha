package sriracha.simulator.parser;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.*;
import sriracha.simulator.model.interfaces.ICollectElements;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysis;
import sriracha.simulator.solver.analysis.ac.ACAnalysis;
import sriracha.simulator.solver.analysis.ac.ACSubType;
import sriracha.simulator.solver.output.filtering.*;

import java.util.*;

public class CircuitBuilder {
    
    HashMap<String, SubCircuitTemplate> subcircuitTemplates = new HashMap<String, SubCircuitTemplate>();
    
    Circuit circuit;
    ArrayList<IAnalysis> analysisTypes = new ArrayList<IAnalysis>();
    ArrayList<OutputFilter> outputFilters = new ArrayList<OutputFilter>();

    public Circuit getCircuit() {
        return circuit;
    }

    public List<IAnalysis> getAnalysisTypes() {
        return Collections.unmodifiableList(analysisTypes);
    }

    public List<OutputFilter> getOutputFilters() {
        return Collections.unmodifiableList(outputFilters);
    }

    public CircuitBuilder(String netlist)
    {
        circuit = new Circuit();
        String[] lines = netlist.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            if (line.length() == 0 || line.charAt(0) == '*' || Character.isWhitespace(line.charAt(0)))
                continue;
            
            if (line.charAt(0) != '.')
                parseCircuitElement(circuit, line);
            else if (line.startsWith(".SUBCKT"))
            {
                int startingLine = i;
                ArrayList<String> subCircuitLines = new ArrayList<String>();
                while (!lines[i].startsWith(".ENDS"))
                {
                    subCircuitLines.add(lines[i]);
                    i++;
                }
                    
                parseSubCircuitTemplate(subCircuitLines.subList(startingLine, i).toArray(new String[0]));
                i++;    // advance past the .ENDS line
            }
            else if (line.startsWith(".AC") || line.startsWith(".TRAN"))
            {                
                analysisTypes.add(parseAnalysis(line));
            }
            else if (line.startsWith(".PLOT"))
            {
                outputFilters.add(parsePlot(line));
            }
        }
    }

    public OutputFilter parsePlot(String line)
    {
        String[] params = tokenizeLine(line);        

        if (params.length < 3)
            throw new ParseException("Not enough parameters for .PLOT: " + line);

        String plotTypeStr = params[1];
        AnalysisType plotType;
        
        if (plotTypeStr.equalsIgnoreCase("AC"))
            plotType = AnalysisType.AC;
        else if (plotTypeStr.equalsIgnoreCase("DC"))
            plotType = AnalysisType.DC;
        else if (plotTypeStr.equalsIgnoreCase("TRAN") || plotTypeStr.equalsIgnoreCase("NOISE") || plotTypeStr.equalsIgnoreCase("DISTO"))
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
        else 
            throw new ParseException("Invalid Plot analysis format: " + line);
        
        ArrayList<ResultInfo> resultInfoList = new ArrayList<ResultInfo>();
        for (int i = 2; i < params.length; i++)
        {
            char firstChar = Character.toUpperCase(params[i].charAt(0));
            if (firstChar == 'V' || firstChar == 'I')
            {
                DataFormat dataFormat = StringToOutputType(params[i].substring(1, params[i].indexOf('(')), line);
                String[] nodeList = parseBracketContents(params[i].substring(params[i].indexOf('('), params[i].length()));
                
                for (String node : nodeList)
                    if (node.length() == 0)
                        throw new ParseException("Expected a node name: " + line);
                
                if (firstChar == 'V')
                {
                    if (nodeList.length == 1)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0])));
                    else if (nodeList.length == 2)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0]), circuit.getNodeIndex(nodeList[1])));
                    else 
                        throw new ParseException("Voltages can only be requested between 1 or 2 nodes: " + line);
                }
                else if (firstChar == 'I')
                {
                    if (nodeList.length != 1 || Character.toUpperCase(nodeList[0].charAt(0)) != 'V')
                        throw new ParseException("Currents can only be requested at a single voltage source: " + line);
                    
                    resultInfoList.add(new CurrentInfo(dataFormat,  nodeList[0], circuit));
                }                    
            }
            else 
                throw new UnsupportedOperationException("The expression '" + params[i] + "' is not supported. Line: " + line);
        }

        OutputFilter outputFilter = new OutputFilter(plotType);
        for (ResultInfo info : resultInfoList)
            outputFilter.addData(info);
        
        return outputFilter;
    }

    private DataFormat StringToOutputType(String outputString, String line)
    {
        if (outputString.equals(""))
            return DataFormat.Complex;
        else if (outputString.equalsIgnoreCase("R"))
            return DataFormat.Real;
        else if (outputString.equalsIgnoreCase("I"))
            return DataFormat.Imaginary;
        else if (outputString.equalsIgnoreCase("M"))
            return DataFormat.Magnitude;
        else if (outputString.equalsIgnoreCase("P"))
            return DataFormat.Phase;
        else if (outputString.equalsIgnoreCase("DB"))
            return DataFormat.Decibels;
        
        throw new ParseException("Invalid output format: " + line);
    }

    private String[] parseBracketContents(String bracketString)
    {
        bracketString = bracketString.trim();
        
        if (bracketString.charAt(0) != '(' || bracketString.charAt(bracketString.length() - 1) != ')')
            throw new ParseException("Expected an expression enclosed in brackets. Found: " + bracketString);

        // strip brackets
        bracketString = bracketString.substring(1, bracketString.length() - 1);

        return bracketString.split(",\\s*");        
    }
    
    private String[] tokenizeLine(String line) {
        ArrayList<String> params = new ArrayList<String>();

        String currentParam = "";
        int bracketLevel = 0;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isWhitespace(line.charAt(i)) && bracketLevel == 0)
            {
                if (currentParam.length() > 0)
                    params.add(currentParam);
                
                currentParam = "";
            }
            else 
            {
                if (line.charAt(i) == '(')
                    bracketLevel++;
                else if (line.charAt(i) == ')')
                    bracketLevel--;
                
                currentParam += line.charAt(i);
            }
        }

        if (currentParam.length() > 0)
            params.add(currentParam);
        
        if (bracketLevel != 0)
            throw new ParseException("Unmatched brackets: " + line);

        return params.toArray(new String[0]);
    }

    public IAnalysis parseAnalysis(String line)
    {
        if (line.startsWith(".AC"))
            return parseSmallSignal(line);
        else 
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
    }
    
    private ACAnalysis parseSmallSignal(String line)
    {
        String[] params = line.split("\\s+");

        if (params.length != 5)
            throw new ParseException("Incorrect number of parameters for AC analysis: " + line);

        ACSubType subType;
        
        if (params[1].equals("LIN"))
            subType = ACSubType.Linear;
        else if (params[1].equals("OCT"))
            subType = ACSubType.Octave;
        else if (params[1].equals("DEC"))
            subType = ACSubType.Decade;
        else
            throw new ParseException("Invalid scale format. Scale must be LIN, OCT, or DEC: " + line);

        int numPoints = Integer.parseInt(params[2]);
        double rangeStart = Double.parseDouble(params[3]);
        double rangeStop = Double.parseDouble(params[4]);
        
        return new ACAnalysis(subType, rangeStart, rangeStop, numPoints);
    }

    private void parseSubCircuitTemplate(String[] lines)
    {
        String[] params = lines[0].split("\\s+");

        if (params.length < 3)
            throw new ParseException("Not enough parameters for a subcircuit template: " + lines[0]);

        String name = params[1];
        SubCircuitTemplate subCircuit = new SubCircuitTemplate(name, params.length - 2);
        for (int i = 2; i < params.length; i++)
            subCircuit.assignNodeMapping(params[i]);

        // skip the first line (the SUBCKT line)
        for (int i = 1; i < lines.length; i++)
            parseCircuitElement(subCircuit, lines[i]);
        
        subcircuitTemplates.put(name, subCircuit);
    }

    private void parseCircuitElement(ICollectElements elementCollection, String line)
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
                createResistor(elementCollection, params[0], params[1], params[2], params[3]);
                break;
            
            case 'i':
                createCurrentSource(elementCollection, params[0], params[1], params[2], additionalParams);
                break;
            
            case 'v':
                createVoltageSource(elementCollection, params[0], params[1], params[2], additionalParams);
                break;

            case 'l':
                createInductor(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'c':
                createCapacitor(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'x':
                createSubcircuit(elementCollection, params[0], params[params.length - 1], Arrays.copyOfRange(params,  1, params.length - 1));
                break;

            default:
                throw new ParseException("Unrecognized element format: " + elementType);
        }
    }

    private void createSubcircuit(ICollectElements elementCollection, String name, String subcircuitName, String[] nodes) 
    {
        if (!subcircuitTemplates.containsKey(subcircuitName))
            throw new ParseException("Subcircuit template not found: " + subcircuitName);

        SubCircuit sc = new SubCircuit(name, subcircuitTemplates.get(subcircuitName));
        
        int[] nodeIndices = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++)
            nodeIndices[i] = elementCollection.assignNodeMapping(nodes[i]);
        
        sc.setNodeIndices(nodeIndices);
        elementCollection.addElement(sc);
    }

    private void createCurrentSource(ICollectElements elementCollection, String name, String node1, String node2, String... params)
    {
        SourceValue value = findPhasorOrDC(params);

        CurrentSource source;
        if (value.AC != null)
            source = new CurrentSource(name, value.AC);
        else
            source = new CurrentSource(name, value.DC);

        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    private void createVoltageSource(ICollectElements elementCollection, String name, String node1, String node2, String... params)
    {
        SourceValue value = findPhasorOrDC(params);

        VoltageSource source;
        if (value.AC != null)
            source = new VoltageSource(name, value.AC);
        else
            source = new VoltageSource(name, value.DC);

        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    private SourceValue findPhasorOrDC(String... params)
    {
        if (params.length == 1)
            return new SourceValue(Double.parseDouble(params[0]));

        if (params[0].equalsIgnoreCase("DC"))
        {
            return new SourceValue(Double.parseDouble(params[1]));
        } 
        else if (params[0].equalsIgnoreCase("AC")) 
        {
            double amplitude = 1, phase = 0;
            if (params.length >= 2)
                amplitude = Double.parseDouble(params[1]);
            if (params.length >= 3)
                phase = Math.toRadians(Double.parseDouble(params[2]));

            double real = amplitude * Math.cos(phase);
            double imaginary = amplitude * Math.sin(phase);

            return new SourceValue(MathActivator.Activator.complex(real, imaginary));
        } 
        else
            throw new ParseException("Invalid source format: " + params[0]);
    }
    
    private void createResistor(ICollectElements elementCollection, String name, String node1, String node2, String value) {
        Resistor r = new Resistor(name, Double.parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        r.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(r);
    }

    private void createCapacitor(ICollectElements elementCollection, String name, String node1, String node2, String value) {
        Capacitor c = new Capacitor(name, Double.parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        c.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(c);
    }

    private void createInductor(ICollectElements elementCollection, String name, String node1, String node2, String value) {
        Inductor i = new Inductor(name, Double.parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        i.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(i);
    }
    
    private class SourceValue
    {
        public double DC;
        public IComplex AC;
        
        public SourceValue(double dc) { DC = dc; }
        public SourceValue(IComplex ac) { AC = ac; }
    }
}