package sriracha.simulator.solver.output.filtering;

import sriracha.simulator.IPrintData;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.analysis.IResultVector;
import sriracha.simulator.solver.output.FilteredVector;
import sriracha.simulator.solver.output.PrintData;

import java.util.ArrayList;

public class OutputFilter {

    private ArrayList<ResultInfo> requestedInfo;

    private AnalysisType analysisType;

    public OutputFilter(AnalysisType analysisType) {
        this.analysisType = analysisType;
        requestedInfo = new ArrayList<ResultInfo>();
    }

    public void addData(ResultInfo info){
        if(!requestedInfo.contains(info))
            requestedInfo.add(info);
    }

    public void removeData(ResultInfo info){
        if(requestedInfo.contains(info))
            requestedInfo.remove(info);
    }

    public void clearFilter(){
        requestedInfo.clear();
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    @Override
    public String toString() {
        return analysisType + ": " + requestedInfo;
    }

    public IPrintData getPlot(IAnalysisResults results) {
        if(requestedInfo.size() == 0) return null;

        PrintData data = new PrintData();
        for(IResultVector vector : results.getData()){
            FilteredVector fVector = new FilteredVector(requestedInfo.size());
            fVector.setX(vector.getX());

            int i =0;
            for(ResultInfo info : requestedInfo){
                fVector.put(i++, info.extractFrom(vector.getData()));
            }

            data.addResult(fVector);
        }
        return data;
    }
}
