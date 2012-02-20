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
}
