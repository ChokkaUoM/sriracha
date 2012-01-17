package sriracha.math.interfaces;

public interface IRealVector extends IVector{
    public double getValue(int i);
    public void setValue(int i, double value);
    public IComplexVector times_j();
}
