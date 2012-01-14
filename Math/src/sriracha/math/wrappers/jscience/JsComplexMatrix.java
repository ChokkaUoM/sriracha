package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.ComplexMatrix;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IVector;


class JsComplexMatrix extends JsMatrix implements IComplexMatrix {

    private Complex[][] values;


    /**
     * Creates an n x m Complex matrix
     * */
    public JsComplexMatrix(int n, int m) {
        values = new Complex[n][m];
    }

    JsComplexMatrix(ComplexMatrix m)
    {
        matrix = m;
    }

    @Override
    public IComplex getValue(int i, int j) {
        return new JsComplex (((ComplexMatrix)matrix).get(i, j));
    }

    @Override
    public void setValue(int i, int j, IComplex value) {
        if(value instanceof JsComplex){
            values[i][j] =((JsComplex) value).value;
        } else{
            values[i][i] = Complex.valueOf(value.getReal(), value.getImag());
        }
        
    }


    @Override
    public IMatrix plus(IMatrix m) {
        if(m instanceof JsMatrix){
            ComplexMatrix sum = ComplexMatrix.valueOf(matrix.plus(((JsMatrix)m).matrix));

        }

        return null;
    }

    @Override
    public IMatrix minus(IMatrix m) {
        return null;
    }

    @Override
    public IMatrix times(IMatrix m) {
        return null;
    }

    @Override
    public IMatrix times(double n) {
        return new JsComplexMatrix(ComplexMatrix.valueOf(matrix.times(Float64.valueOf(n))));
    }

    @Override
    public IVector solve(IVector b) {
        return null;
    }
}
