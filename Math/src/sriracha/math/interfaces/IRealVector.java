package sriracha.math.interfaces;

public interface IRealVector extends IVector
{
    public double getValue(int i);

    public void setValue(int i, double value);

    public void addValue(int i, double value);


    @Override
    public IRealVector clone();
}
