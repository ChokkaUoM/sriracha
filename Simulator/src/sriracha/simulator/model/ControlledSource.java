package sriracha.simulator.model;

/**
 * Base class for current sources.
 * Uses small signal model
 */
public abstract class ControlledSource extends Source {

    /**
     * nodes
     */
    protected int i, iPrime, k, kPrime;

    //factor for source
    protected double gm;


    /**
     * Constructor for controlled sources
     *
     * @param i      - top left node in ssm
     * @param iPrime - bottom left node in ssm
     * @param k      - top right node in ssm
     * @param kPrime - bottom right node in ssm
     * @param gm     - factor in source equation
     */
    protected ControlledSource(int i, int iPrime, int k, int kPrime, double gm) {
        this.i = i;
        this.iPrime = iPrime;
        this.k = k;
        this.kPrime = kPrime;
        this.gm = gm;
    }
}
