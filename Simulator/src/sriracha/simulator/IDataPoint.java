package sriracha.simulator;

public interface IDataPoint {
    public double getXValue();
    public double[][] getVector();

    /**
     * Computes the total size of the vector in # of entries
     * @return total length of unrolled vector
     */
    public int totalVectorLength();
}
