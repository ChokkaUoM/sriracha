package sriracha.frontend.android.results.functions;

public final class Cos extends Function
{

    public Cos()
    {
    }

    public Cos(Function nestedFunction)
    {
        super(nestedFunction);
    }

    @Override
    public double evaluate(double x)
    {
        return Math.cos(super.evaluate(x));
    }
}
