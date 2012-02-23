package sriracha.simulator.solver.output;

import sriracha.simulator.IDataPoint;

import java.text.DecimalFormat;

public class FilteredVector implements IDataPoint {


    double x;
    double[][] data;

    public FilteredVector(int length) {
        data = new double[length][];
    }

    public void put(int i, double[] value) {
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
        int l = 0;
        for (double[] da : data) {
            l += da.length;
        }
        return l;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(format(x) + "\t");
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                sb.append(format(data[i][j]));
                if (j + 1 < data[i].length || i + 1 < data.length) sb.append("\t");
            }
        }

        return sb.toString() + "\n";
    }

    private static String format(double val) {
        DecimalFormat format = new DecimalFormat("0.000000E00");
        return format.format(val);
    }
}
