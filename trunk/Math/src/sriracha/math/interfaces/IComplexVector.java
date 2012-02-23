package sriracha.math.interfaces;

public interface IComplexVector extends IVector {

    public IComplex getValue(int i);

    public void setValue(int i, IComplex value);

    public void addValue(int i, IComplex value);
}
