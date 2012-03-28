package sriracha.frontend.android.results.functions;

public class Function
{
    
    private Function nestedFunction = null;
    
    private double value = Double.NaN;

    public Function(){
    }

    public Function(double value){
        this.value = value;
    }
    
    public Function(Function nestedFunction)
    {
        this.nestedFunction = nestedFunction;
    }
    
    public double evaluate(double x){
        if(nestedFunction != null)
            return nestedFunction.evaluate(x);
        if(value != Double.NaN)
            return value;
        return x;
    }
    
    //special sum of functions function.
    private class Sum extends Function{

        Function other;
        
        public Sum(Function a, Function b)
        {
            super(a);
            other = b;
        }

        @Override
        public double evaluate(double x)
        {
            return super.evaluate(x) + other.evaluate(x);    
        }
    }
    
    private class Opposite extends Function{
        private Opposite(Function nestedFunction)
        {
            super(nestedFunction);
        }

        @Override
        public double evaluate(double x)
        {
            return -super.evaluate(x);    
        }
    }

    private class Inverse extends Function{
        private Inverse(Function nestedFunction)
        {
            super(nestedFunction);
        }

        @Override
        public double evaluate(double x)
        {
            return 1./super.evaluate(x);
        }
    }

    //special product of functions function.
    private class Product extends Function{

        Function other;

        public Product(Function a, Function b)
        {
            super(a);
            other = b;
        }

        @Override
        public double evaluate(double x)
        {
            return super.evaluate(x) * other.evaluate(x);
        }
    }
    
    public Function plus(Function f){
        return new Sum(this, f);
    }

    public Function minus(Function f){
        return new Sum(this, f.opposite());
    }

    public Function div(Function f){
        return new Product(this, f.inverse());
    }

    public Function times(Function f){
        return new Product(this, f);
    }
    
    public Function opposite(){
        return new Opposite(this);
    }

    public Function inverse(){
        return new Inverse(this);
    }
    
    
}
