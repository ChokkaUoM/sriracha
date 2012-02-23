package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Matrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IVector;

public class JsRealMatrix extends JsMatrix implements IRealMatrix {


    public JsRealMatrix(int m, int n) {
        matrix = Float64Matrix.valueOf(new double[m][n]);
    }

    JsRealMatrix(Float64Matrix m) {
        matrix = m;
    }


    Float64Matrix getMatrix() {
        return (Float64Matrix) matrix;
    }


    @Override
    public IVector solve(IVector b) {
        if (b instanceof JsComplexVector) {
            return new JsComplexVector(makeComplex(this).getMatrix().solve(((JsComplexVector) b).getVector()));
        } else if (b instanceof JsRealVector) {
            return new JsRealVector(getMatrix().solve(((JsRealVector) b).getVector()));
        }

        return null;
    }

    @Override
    public double getValue(int i, int j) {
        return getMatrix().get(i, j).doubleValue();
    }

    @Override
    public void setValue(int i, int j, double value) {
        getMatrix().set(i, j, Float64.valueOf(value));
    }

    @Override
    public void addValue(int i, int j, double value) {
        Float64 previousValue = getMatrix().get(i, j);
        getMatrix().set(i, j, previousValue.plus(value));

    }


    @Override
    public IMatrix clone() {
        return new JsRealMatrix(getMatrix().copy());
    }

}
