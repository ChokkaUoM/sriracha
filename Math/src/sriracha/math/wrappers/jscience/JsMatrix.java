package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.vector.Matrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IVector;

abstract class JsMatrix implements IMatrix{
    
    protected Matrix matrix;
    
/*    protected int rows, columns;
    
    
    private JsMatrix(Matrix m){
        matrix = m;
    }

    public JsMatrix(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
    }


    protected void buildMatrix(){}
    
    @Override
    public IMatrix plus(IMatrix m) {
        if(m instanceof JsMatrix){
            JsMatrix that = (JsMatrix)m;
            buildMatrix();
            return new JsMatrix(matrix.plus(that.matrix));
        }

        return null;
    }

    @Override
    public IMatrix minus(IMatrix m) {
        if(m instanceof JsMatrix){
            JsMatrix that = (JsMatrix)m;
            buildMatrix();
            return new JsMatrix(matrix.minus(that.matrix));
        }

        return null;
    }

    @Override
    public IMatrix times(IMatrix m) {
        if(m instanceof JsMatrix){
            JsMatrix that = (JsMatrix)m;
            buildMatrix();
            return new JsMatrix(matrix.times(that.matrix));
        }

        return null;
    }

    @Override
    public IVector solve(IVector b) {

    }*/
}
