package sriracha.math.interfaces;

public interface IVector {

    public int getDimension();

    public IVector plus(IVector vector);

    public IVector minus(IVector vector);

    public IVector times(double d);

    public IVector clone();


    public IVector opposite();
}
