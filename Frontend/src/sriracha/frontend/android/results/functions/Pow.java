package sriracha.frontend.android.results.functions;

public class Pow extends Function
{
    private Function exponent;

    public Pow(Function base, Function exponent)
    {
        super(base);
        this.exponent = exponent;
    }

    public Pow(Function base, double exponent)
    {
        this(base, new Function(exponent));
    }

    @Override
    public double evaluate(double x)
    {
        return Math.pow(super.evaluate(x), exponent.evaluate(x));    //To change body of overridden methods use File | Settings | File Templates.
    }
}
