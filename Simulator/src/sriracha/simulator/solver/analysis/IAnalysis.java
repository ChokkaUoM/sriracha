package sriracha.simulator.solver.analysis;

import sriracha.simulator.solver.IEquation;

public interface IAnalysis {
    public AnalysisType getSubType();

    public IAnalysisResults analyse(IEquation equation);
}
