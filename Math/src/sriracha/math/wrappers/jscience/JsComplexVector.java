package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IVector;

class JsComplexVector extends JsVector implements IComplexVector {

    public JsComplexVector(int dimension) {
        vector = buildZeroVector(dimension);
    }

    JsComplexVector(ComplexVector vector) {
        this.vector = vector;
    }

    JsComplexVector(Vector<Complex> vector) {
        this.vector = ComplexVector.valueOf(vector);
    }


    private ComplexVector buildZeroVector(int dim) {
        Complex arr[] = new Complex[dim];
        for (int i = 0; i < dim; i++) {
            arr[i] = Complex.ZERO;
        }

        return ComplexVector.valueOf(arr);
    }


    @Override
    public IVector times(double d) {
        return new JsComplexVector(getVector().times(Complex.valueOf(d, 0)));
    }

    @Override
    public IVector opposite() {
        return new JsComplexVector(getVector().opposite());
    }

    @Override
    public IVector plus(IVector v) {
        if (v instanceof JsComplexVector) {
            return new JsComplexVector(((JsComplexVector) v).getVector().plus(getVector()));
        } else if (v instanceof JsRealVector) {
            return plus(makeComplex((JsRealVector) v));
        }
        return null;
    }

    @Override
    ComplexVector getVector() {
        return (ComplexVector) vector;
    }

    @Override
    public IComplex getValue(int i) {
        return new JsComplex(((ComplexVector) vector).get(i));
    }

    @Override
    public void setValue(int i, IComplex value) {
        getVector().set(i, JsComplex.make(value));
    }

    @Override
    public void addValue(int i, IComplex value) {

        getVector().set(i, getVector().get(i).plus(JsComplex.make(value)));
    }

    @Override
    public IVector clone() {
        return new JsComplexVector(ComplexVector.valueOf(vector.copy()));
    }
}
