package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.ComplexMatrix;
import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Matrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IVector;

abstract class JsMatrix implements IMatrix{
    
    protected Matrix matrix;


    @Override
    public IMatrix plus(IMatrix m) {
        if(m instanceof JsMatrix){

            if(m instanceof JsComplexMatrix || this instanceof JsComplexMatrix){
                return  new JsComplexMatrix(ComplexMatrix.valueOf(matrix.plus(((JsMatrix)m).matrix)));
            }  else {
                return new JsRealMatrix(Float64Matrix.valueOf(matrix.plus(((JsMatrix)m).matrix)));
            }
        }

        return null;
    }

    @Override
    public IMatrix minus(IMatrix m) {
        if(m instanceof JsMatrix){
            
            if(m instanceof JsComplexMatrix || this instanceof JsComplexMatrix){
                return  new JsComplexMatrix(ComplexMatrix.valueOf(matrix.minus(((JsMatrix)m).matrix)));
            }  else {
                return new JsRealMatrix(Float64Matrix.valueOf(matrix.minus(((JsMatrix)m).matrix)));
            }
        }

        return null;
    }

    @Override
    public IMatrix times(IMatrix m) {
        if(m instanceof JsMatrix){

            if(m instanceof JsComplexMatrix || this instanceof JsComplexMatrix){
                return  new JsComplexMatrix(ComplexMatrix.valueOf(matrix.times(((JsMatrix)m).matrix)));
            }  else {
                return new JsRealMatrix(Float64Matrix.valueOf(matrix.times(((JsMatrix)m).matrix)));
            }
        }

        return null;
    }

    @Override
    public IMatrix times(double n) {
        if(this instanceof JsComplexMatrix){
            return new JsComplexMatrix(ComplexMatrix.valueOf(matrix.times(Float64.valueOf(n))));
        }  else {
            return new JsRealMatrix(Float64Matrix.valueOf(matrix.times(Float64.valueOf(n))));
        }
    }
    
    @Override
    public IVector solve(IVector b){
        if(b instanceof JsVector) {
            //todo: insert code here
        }

        return null;

    }
}
