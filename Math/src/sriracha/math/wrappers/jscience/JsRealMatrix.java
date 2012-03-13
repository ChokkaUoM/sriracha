package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Matrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.math.interfaces.IVector;

import java.text.DecimalFormat;

class JsRealMatrix extends JsMatrix implements IRealMatrix
{


    public JsRealMatrix(int m, int n)
    {
        matrix = Float64Matrix.valueOf(new double[m][n]);
    }

    JsRealMatrix(Float64Matrix m)
    {
        matrix = m;
    }


    Float64Matrix getMatrix()
    {
        return (Float64Matrix) matrix;
    }


    public IRealVector solve(IRealVector vector)
    {
        if (vector instanceof JsRealVector)
        {
            return new JsRealVector(getMatrix().solve(((JsRealVector) vector).getVector()));
        }

        return null;
    }

    public IComplexVector solve(IComplexVector vector)
    {
        if (vector instanceof JsComplexVector)
        {
            return new JsComplexVector(makeComplex(this).getMatrix().solve(((JsComplexVector) vector).getVector()));
        }
        return null;
    }


    @Override
    public IVector solve(IVector b)
    {
        if (b instanceof IComplexVector)
        {
            return solve((IComplexVector) b);
        } else if (b instanceof IRealVector)
        {
            return solve((IRealVector) b);
        }

        return null;
    }

    @Override
    public double getValue(int i, int j)
    {
        return getMatrix().get(i, j).doubleValue();
    }

    @Override
    public void setValue(int i, int j, double value)
    {
        getMatrix().set(i, j, Float64.valueOf(value));
    }

    @Override
    public void addValue(int i, int j, double value)
    {
        Float64 previousValue = getMatrix().get(i, j);
        getMatrix().set(i, j, previousValue.plus(value));

    }

    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("+0.0000;-0.0000");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.getNumberOfRows(); i++)
        {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++)
            {
                sb.append(format.format(getMatrix().get(i, j)) + "\t");
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    @Override
    public IRealMatrix clone()
    {
        return new JsRealMatrix(getMatrix().copy());
    }

}
