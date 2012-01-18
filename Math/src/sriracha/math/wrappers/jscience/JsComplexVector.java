package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IVector;

class JsComplexVector extends JsVector implements IComplexVector{
    
    public JsComplexVector(int dimension){
        vector = buildZeroVector(dimension);
    }
    
    JsComplexVector(ComplexVector vector){
        this.vector = vector;
    }


    private ComplexVector buildZeroVector(int dim){
        Complex arr[] = new Complex[dim];
        for(int i = 0; i < dim; i++){
            arr[i] = Complex.ZERO;
        }

        return ComplexVector.valueOf(arr);
    }
    
    @Override
    public IComplex getValue(int i) {
        return new JsComplex (((ComplexVector)vector).get(i));
    }

    @Override
    public void setValue(int i, IComplex value) {
        ((ComplexVector)vector).set(i, Complex.valueOf(value.getReal(), value.getImag()));
    }

    @Override
    public IVector clone() {
        return new JsComplexVector(ComplexVector.valueOf(vector.copy()));
    }
}
