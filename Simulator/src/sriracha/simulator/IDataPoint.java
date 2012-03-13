package sriracha.simulator;

/**
 * Corresponds to 1 X value and a vector of Y values, this is one of the data points requested
 * from a .PRINT statement.
 */
public interface IDataPoint {

    /**
     * gets value for X axis, the value for which the
     * current y vector was computed
     * @return x value
     */
    public double getXValue();

    /**
     * Vector of Y values corresponding to the X value,
     * each double[] in the vector corresponds to one request in the
     * .PRINT statement, if it was for a complex quantity then
     * there are 2 entries in that array, otherwise only 1.
     * @return y data vector
     */
    public double[][] getVector();

    /**
     * Computes the total size of the vector in # of entries
     * This does not correspond to the # of requested points since complex quantities count as 2
     * in this method
     * @return total length of unrolled vector
     */
    public int totalVectorLength();
}
