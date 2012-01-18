package sriracha.math.wrappers.jscience;

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

    Vector GetVector(){
        return vector;
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
