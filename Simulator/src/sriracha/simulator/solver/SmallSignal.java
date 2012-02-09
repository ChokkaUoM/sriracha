package sriracha.simulator.solver;

import sriracha.simulator.solver.interfaces.IAnalysis;

public class SmallSignal implements IAnalysis {

    private SSType type;

    private double fStart;
    private double fEnd;
    private int points;

    /**
     *
     * @param type Type of scale for output octave and decade are log scales
     * @param fStart start frequency
     * @param fEnd stop frequency
     * @param points for Linear total number of frequency points or number per decade/octave
     */
    public SmallSignal(SSType type, double fStart, double fEnd, int points) {
        this.type = type;
        this.fStart = fStart;
        this.fEnd = fEnd;
        this.points = points;
    }


    public SSType getType() {
        return type;
    }

    public double getfStart() {
        return fStart;
    }

    public double getfEnd() {
        return fEnd;
    }

    public int getPoints() {
        return points;
    }

}
