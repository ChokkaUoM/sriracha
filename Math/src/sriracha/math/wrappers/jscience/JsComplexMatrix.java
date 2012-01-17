package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexMatrix;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IVector;


class JsComplexMatrix extends JsMatrix implements IComplexMatrix {

    /**
     * Creates an n x m Complex matrix
     * */
    public JsComplexMatrix(int n, int m) {
        matrix = ComplexMatrix.valueOf(new Complex[m][n]);
    }

    JsComplexMatrix(ComplexMatrix m){
        matrix = m;
    }

    @Override
    public IComplex getValue(int i, int j) {
        return new JsComplex (((ComplexMatrix)matrix).get(i, j));
    }

    @Override
    public void setValue(int i, int j, IComplex value) {
        if(value instanceof JsComplex){
            matrix.set(i, j, ((JsComplex) value).value);
        } else{
            matrix.set(i, j, Complex.valueOf(value.getReal(), value.getImag()));
        }
        
    }


}
