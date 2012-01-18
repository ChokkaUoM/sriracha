package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Vector;
import sriracha.math.interfaces.IRealVector;
import sriracha.math.interfaces.IVector;

public class JsRealVector extends JsVector implements IRealVector{


    public JsRealVector(int dimension){
        vector = Float64Vector.valueOf(new double[dimension]);
    }

    JsRealVector(Float64Vector vector){
        this.vector = vector;
    }


    @Override
    public double getValue(int i) {
        return ((Float64Vector)vector).getValue(i);
    }

    @Override
    public void setValue(int i, double value) {
        ((Float64Vector)vector).set(i, Float64.valueOf(value));
    }

    @Override
    public IVector clone() {
        return new JsRealVector(Float64Vector.valueOf(vector.copy()));
    }
}
