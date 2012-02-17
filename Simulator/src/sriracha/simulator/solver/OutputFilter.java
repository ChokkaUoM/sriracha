package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.interfaces.OutputData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OutputFilter {

    private ArrayList<OutputData> dataFilter;

    private AnalysisType analysisType;

    public OutputFilter(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public void addData(OutputData data){
        if(!dataFilter.contains(data))
            dataFilter.add(data);
    }


    public void removeData(OutputData data){
        if(dataFilter.contains(data))
            dataFilter.remove(data);
    }

    public void clearFilter(){
        dataFilter.clear();
    }

    /**
     * sends filtered data out the stream
     * @param solution
     * @param omega current frequency value - outputted as well??
     */
    void flush(DataOutputStream dataOut, IComplexVector solution, double omega) {
        try {
            //todo: determine exact output spec
            //dataOut.writeDouble(omega); //yes or no?
            if(dataFilter.size() == 0){
                for (int i = 0; i < solution.getDimension(); i++) {
                    dataOut.writeDouble(solution.getValue(i).getReal());
                    dataOut.writeDouble(solution.getValue(i).getImag());
                }
            }else {
                //so that the number are output in the same order they were inserted into the filter
                for(OutputData d : dataFilter){
                    double[] val  = d.extract(solution);
                    for(double v : val){
                        dataOut.writeDouble(v);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



    public AnalysisType getAnalysisType() {
        return analysisType;
    }
}
