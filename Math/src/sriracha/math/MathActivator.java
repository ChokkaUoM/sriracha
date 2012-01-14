package sriracha.math;

import sriracha.math.interfaces.*;
import sriracha.math.wrappers.jscience.JsMathActivator;

public abstract class MathActivator {
	
	
	public static MathActivator Activator = new JsMathActivator();

	public abstract IComplexMatrix complexMatrix(int i, int j);
    public abstract IRealMatrix realMatrix(int i, int j);

	public abstract IVector vector(int length);
	

}
