package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Vector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IRealVector;
import sriracha.math.interfaces.IVector;

public class JsRealVector extends JsVector implements IRealVector {


    public JsRealVector(int dimension) {
        vector = Float64Vector.valueOf(new double[dimension]);
    }

    JsRealVector(Float64Vector vector) {
        this.vector = vector;
    }

    JsRealVector(Vector<Float64> vector) {
        this.vector = Float64Vector.valueOf(vector);
    }


    @Override
    public double getValue(int i) {
        return getVector().getValue(i);
    }

    @Override
    public void setValue(int i, double value) {
        getVector().set(i, Float64.valueOf(value));
    }

    @Override
    public void addValue(int i, double value) {
        setValue(i, getValue(i) + value);
    }

    @Override
    public IVector clone() {
        return new JsRealVector(Float64Vector.valueOf(vector.copy()));
    }

    Float64Vector getVector() {
        return (Float64Vector) vector;
    }
}
