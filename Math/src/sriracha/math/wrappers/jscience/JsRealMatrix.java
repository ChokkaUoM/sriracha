package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexMatrix;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IVector;

public class JsRealMatrix extends JsMatrix implements IRealMatrix{
    @Override
    public double getValue(int i, int j) {
        return 0;
    }

    @Override
    public void setValue(int i, int j, double value) {

    }

    @Override
    public IComplexMatrix times_j() {
        return new JsComplexMatrix(ComplexMatrix.valueOf( matrix.times(Complex.I)));
    }

    @Override
    public IMatrix plus(IMatrix m) {
        return null;
    }

    @Override
    public IMatrix minus(IMatrix m) {
        return null;
    }

    public JsRealMatrix(int i, int j) {
    }

    @Override
    public IMatrix times(IMatrix m) {
        return null;
    }

    @Override
    public IMatrix times(double n) {
        return null;
    }

    @Override
    public IVector solve(IVector b) {
        return null;
    }
}
