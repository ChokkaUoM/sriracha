package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;

class JsComplexVector extends JsVector implements IComplexVector{
    
    public JsComplexVector(int dimension){
        vector = ComplexVector.valueOf(new Complex[dimension]);
    }
    
    JsComplexVector(ComplexVector vector){
        this.vector = vector;
    }
    
    
    
    @Override
    public IComplex getValue(int i) {
        return new JsComplex (((ComplexVector)vector).get(i));
    }

    @Override
    public void setValue(int i, IComplex value) {
        ((ComplexVector)vector).set(i, Complex.valueOf(value.getReal(), value.getImag()));
    }
}
