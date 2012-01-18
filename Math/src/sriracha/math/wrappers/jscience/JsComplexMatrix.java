package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexMatrix;
import sriracha.math.interfaces.*;


class JsComplexMatrix extends JsMatrix implements IComplexMatrix {

    
    /**
     * Creates an mxn Complex matrix
     * */
    public JsComplexMatrix(int m, int n) {
        matrix = buildZeroMatrix(m, n);
    }

    JsComplexMatrix(ComplexMatrix m){
        matrix = m;
    }

    ComplexMatrix getMatrix(){
        return (ComplexMatrix)matrix;
    }

    private ComplexMatrix buildZeroMatrix(int m, int n){
        Complex arr[][] = new Complex[m][n];
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                arr[i][j] = Complex.ZERO;
            }
        }

        return ComplexMatrix.valueOf(arr);
    }



    @Override
    public IComplex getValue(int i, int j) {
        return new JsComplex (getMatrix().get(i, j));
    }

    @Override
    public void setValue(int i, int j, IComplex value) {
        if(value instanceof JsComplex){
            matrix.set(i, j, ((JsComplex) value).value);
        } else{
            matrix.set(i, j, Complex.valueOf(value.getReal(), value.getImag()));
        }
        
    }

    @Override
    public IComplexVector solve(IVector b){
        if(b instanceof JsRealVector){
            JsComplexVector v =  JsVector.makeComplex((JsRealVector)b);
            return new JsComplexVector(getMatrix().solve(v.getVector()));
        }else{
            return new JsComplexVector(getMatrix().solve(((JsComplexVector)b).getVector()));
        }
    }

    @Override
    public IMatrix clone() {
        return new JsComplexMatrix(getMatrix().copy());
    }
}
