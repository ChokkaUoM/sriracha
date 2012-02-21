package sriracha.simulator.solver.output;

import sriracha.simulator.IDataPoint;

public class FilteredVector implements IDataPoint {


    double x;
    double[][] data;

    public FilteredVector(int length) {
        data = new double[length][];
    }

    public void put(int i, double[] value){
        data[i] = value;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getXValue() {
        return x;
    }

    @Override
    public double[][] getVector() {
        return data;
    }

    @Override
    public int totalVectorLength() {
        int l =0;
        for(double[] da : data){
            l+=da.length;
        }
        return l;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        for(int i =0; i< data.length; i++){
            sb.append('[');
            for(int j =0; j< data[i].length; j++){
                sb.append(Math.round(data[i][j]*100)/100.0);
                if(j +1 < data[i].length) sb.append(", ");
            }
            sb.append(']');
        }
        
        return x + ": " + sb.toString() + "\n";
    }
}
