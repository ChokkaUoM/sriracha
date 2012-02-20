package sriracha.simulator.solver.analysis.ac;

import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.IEquation;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysis;

public class ACAnalysis implements IAnalysis {

    private ACSubType subType;

    private double fStart;
    private double fEnd;
    private int points;

    /**
     * @param subType Type of scale for output octave and decade are log scales
     * @param fStart  start frequency
     * @param fEnd    stop frequency
     * @param points  for Linear total number of frequency points or number per decade/octave
     */
    public ACAnalysis(ACSubType subType, double fStart, double fEnd, int points) {
        this.subType = subType;
        this.fStart = fStart;
        this.fEnd = fEnd;
        this.points = points;
    }


    /**
     * Helper method for analyse(IEquation)
     * @param base
     * @param equation
     * @return
     */
    private ACResults logScaleRun(int base, IEquation equation) {
        ACResults results = new ACResults();
        double f = fStart;
        int i = 0, k = 1;
        while (f <= fEnd) {
            double interval = (f * k) * (base - 1) / points;
            while (i < points && f <= fEnd) {
                f = f + i * interval;
                IComplexVector soln = equation.solve(f);
                results.addVector(f, soln);
                i++;
            }
            k *= base;
            i = 0;
        }
        return  results;
    }

    @Override
    public ACResults analyse(IEquation equation) {
        ACResults results = new ACResults();
        switch (subType) {
            case Linear:
                double interval = (fEnd - fStart) / points;
                for (int i = 0; i < points; i++) {
                    double omega = fStart + interval * i;
                    IComplexVector soln = equation.solve(omega);
                    results.addVector(omega, soln);

                }
                break;
            case Decade:
                return logScaleRun(10, equation);
            case Octave:
                return logScaleRun(8, equation);
        }
        return results;
    }

    @Override
    public AnalysisType getSubType() {
        return AnalysisType.AC;
    }

    @Override
    public String toString() {
        return ".AC " + subType + " " + points + " " + fStart + " " + fEnd;
    }
}
