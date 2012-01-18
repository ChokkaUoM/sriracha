package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import org.jscience.mathematics.vector.Float64Vector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IVector;

public abstract class JsVector implements IVector{
    
    protected Vector vector;

    JsVector(Vector vector){
        this.vector = vector;
    }

    JsVector(){}

    Vector getVector(){
        return vector;
    }


    protected static JsComplexVector makeComplex(JsRealVector vector){
        Float64Vector realVector = vector.getVector();
        int dim  = realVector.getDimension();

        Complex[] values = new Complex[dim];

        for(int i = 0; i < dim; i++){
            values[i] = Complex.valueOf(realVector.get(i).doubleValue(), 0);
        }

        return new JsComplexVector(ComplexVector.valueOf(values));
    }


    @Override
    public int getDimension() {
        return vector.getDimension();
    }

    @Override
    public IVector plus(IVector v) {
        if(v instanceof JsVector){

            if(this instanceof JsComplexVector || v instanceof JsComplexVector){
                return new JsComplexVector(ComplexVector.valueOf(this.vector.plus(((JsVector)v).vector)));
            }



            return new JsRealVector(Float64Vector.valueOf(this.vector.plus(((JsVector)v).vector)));
        }
                return null;
    }

    @Override
    public IVector minus(IVector vector) {
        return null;
    }

    @Override
    public IVector times(double vector) {
        return null;
    }

    @Override
    public abstract IVector clone();
}
