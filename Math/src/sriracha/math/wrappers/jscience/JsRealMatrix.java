package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.ComplexMatrix;
import org.jscience.mathematics.vector.Float64Matrix;
import sriracha.math.interfaces.*;

public class JsRealMatrix extends JsMatrix implements IRealMatrix{




   public JsRealMatrix(int m, int n){
       matrix = Float64Matrix.valueOf(new double[m][n]);
   }

    JsRealMatrix(Float64Matrix m){
        matrix = m;
    }



    Float64Matrix getMatrix(){
        return (Float64Matrix)matrix;
    }


    @Override
    public IVector solve(IVector b) {
        if(b instanceof JsComplexVector){
             return  new JsComplexVector(makeComplex(this).getMatrix().solve(((JsComplexVector) b).getVector()));
        }else if(b instanceof JsRealVector){
            JsRealVector vect = new JsRealVector(getMatrix().solve(((JsRealVector)b).getVector()));
            return vect;
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

  /*  @Override
    public IComplexMatrix times_j() {
        Float64Matrix m = (Float64Matrix)matrix;
        foreach
        
        return new JsComplexMatrix(ComplexMatrix.valueOf(matrix).times(Complex.I));
    }*/

    @Override
    public IMatrix clone() {
        return new JsRealMatrix(getMatrix().copy());
    }
}
