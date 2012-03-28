package sriracha.frontend.android.results.functions;

public final class Sin extends Function
{

    public Sin()
    {
    }

    public Sin(Function nestedFunction)
    {
        super(nestedFunction);
    }

    @Override
    public double evaluate(double x)
    {
        return Math.sin(super.evaluate(x));
    }
}
