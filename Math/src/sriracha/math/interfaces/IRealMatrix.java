package sriracha.math.interfaces;

public interface IRealMatrix extends IMatrix{
    public double getValue(int i, int j);
    public void setValue(int i, int j, double value);

}
