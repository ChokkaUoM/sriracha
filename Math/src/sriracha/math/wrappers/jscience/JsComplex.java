package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import sriracha.math.interfaces.IComplex;

class JsComplex implements IComplex{

    public Complex value;

    public JsComplex(double real, double imag){
        value = Complex.valueOf(real, imag);
    }

    public JsComplex(Complex c){
        value = c;
    }



    @Override
    public double getImag() {
        return value.getImaginary();
    }

    @Override
    public void setImag(double complex) {
        value = Complex.valueOf(value.getReal(), complex);
    }

    @Override
    public double getReal() {
        return value.getReal();
    }

    @Override
    public void setReal(double real) {
          value = Complex.valueOf(real, value.getImaginary());
    }
    
}
