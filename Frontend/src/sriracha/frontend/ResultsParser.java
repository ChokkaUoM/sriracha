package sriracha.frontend;

import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;
import sriracha.simulator.IDataPoint;
import sriracha.simulator.IPrintData;

import java.util.ArrayList;

public class ResultsParser {
    
    public ArrayList<Plot> getPlots(IPrintData printData){
        ArrayList<Plot> plots = new ArrayList<Plot>();
        int plotCount = printData.getData().get(0).totalVectorLength();
        for(int i = 0; i<plotCount; i++){
            plots.add(new Plot());
        }

        for(IDataPoint dp : printData.getData()){
            double x = dp.getXValue();
            double [][]vector = dp.getVector();
            int k =0;
            for(int i = 0; i < vector.length; i++){
                for(int j = 0; j < vector[i].length; j++){
                    plots.get(k++).addPoint(new Point(x, vector[i][j]));
                }
            }
        }

        return plots;
    }
}
