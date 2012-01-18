package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.ComplexMatrix;
import org.jscience.mathematics.vector.Float64Matrix;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IVector;

public class JsRealMatrix extends JsMatrix implements IRealMatrix{




   public JsRealMatrix(int m, int n){
       matrix = Float64Matrix.valueOf(new double[m][n]);
   }

    JsRealMatrix(Float64Matrix m){
        matrix = m;
    }







    @Override
    public double getValue(int i, int j) {
        return ((Float64Matrix)matrix).get(i, j).doubleValue();
    }

    @Override
    public void setValue(int i, int j, double value) {
        ((Float64Matrix)matrix).set(i, j, Float64.valueOf(value));
    }

  /*  @Override
    public IComplexMatrix times_j() {
        Float64Matrix m = (Float64Matrix)matrix;
        foreach
        
        return new JsComplexMatrix(ComplexMatrix.valueOf(matrix).times(Complex.I));
    }*/

    @Override
    public IMatrix clone() {
        return new JsRealMatrix(Float64Matrix.valueOf(matrix.copy()));
    }
}
