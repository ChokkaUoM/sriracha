package sriracha.simulator.solver.output.filtering;

import sriracha.simulator.IPrintData;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.analysis.IResultVector;
import sriracha.simulator.solver.output.FilteredVector;
import sriracha.simulator.solver.output.PrintData;

import java.util.ArrayList;

public class OutputFilter
{

    private ArrayList<NodeInfo> requestedInfo;

    private AnalysisType analysisType;


    public OutputFilter(AnalysisType analysisType)
    {
        this.analysisType = analysisType;
        requestedInfo = new ArrayList<NodeInfo>();
    }

    public void addData(NodeInfo info)
    {
        if (!requestedInfo.contains(info))
            requestedInfo.add(info);
    }

    public void removeData(NodeInfo info)
    {
        if (requestedInfo.contains(info))
            requestedInfo.remove(info);
    }

    public void clearFilter()
    {
        requestedInfo.clear();
    }

    public AnalysisType getAnalysisType()
    {
        return analysisType;
    }

    @Override
    public String toString()
    {
        return analysisType + ": " + requestedInfo;
    }

    private String analysisXLabel(AnalysisType analysis)
    {
        switch (analysis)
        {
            case AC:
                return "freq(hz)";
            case DC:
                return "Volts";
            default:
                return null;
        }
    }

    public IPrintData filterResults(IAnalysisResults results)
    {
        if (requestedInfo.size() == 0) return null;

        PrintData data = new PrintData();

        data.addLabel(analysisXLabel(analysisType));

        for (NodeInfo ri : requestedInfo)
        {
            data.addLabel(ri.toString());
        }

        for (IResultVector vector : results.getData())
        {
            FilteredVector fVector = new FilteredVector(requestedInfo.size());
            fVector.setX(vector.getX());

            int i = 0;
            for (NodeInfo info : requestedInfo)
            {
                fVector.put(i++, info.extractFrom(vector.getData()));
            }

            data.addResult(fVector);
        }
        return data;
    }
}
