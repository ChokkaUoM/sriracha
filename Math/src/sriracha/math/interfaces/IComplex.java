package sriracha.math.interfaces;

public interface IComplex {

    public double getImag();

    public void setImag(double imag);

    public double getReal();

    public void setReal(double real);

    IComplex plus(IComplex d);
    IComplex minus(IComplex d);

    IComplex opposite();
}

