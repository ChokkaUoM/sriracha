package sriracha.math.interfaces;

public interface IRealMatrix extends IMatrix {
    public double getValue(int i, int j);

    public void setValue(int i, int j, double value);

    public void addValue(int i, int j, double value);

}
