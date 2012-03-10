package sriracha.math.interfaces;

public interface IRealMatrix extends IMatrix
{
    public double getValue(int i, int j);

    public void setValue(int i, int j, double value);

    public void addValue(int i, int j, double value);

    public IRealVector solve(IRealVector vector);

    public IComplexVector solve(IComplexVector vector);

    @Override
    public IRealMatrix clone();
}
