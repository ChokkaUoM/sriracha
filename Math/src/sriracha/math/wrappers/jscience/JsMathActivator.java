package sriracha.math.wrappers.jscience;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;

public class JsMathActivator extends MathActivator {


    @Override
    public IComplexMatrix complexMatrix(int i, int j) {
        return new JsComplexMatrix(i, j);
    }

    @Override
    public IRealMatrix realMatrix(int i, int j) {
        return new JsRealMatrix(i, j);
    }


    @Override
	public IVector vector(int dimension) {
		// TODO Auto-generated method stub
		return null;
	}

}
