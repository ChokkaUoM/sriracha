package sriracha.simulator.solver.analysis;

import sriracha.simulator.model.Circuit;

public abstract class Analysis
{

    private AnalysisType type;

    public AnalysisType getType()
    {
        return type;
    }

    protected Analysis(AnalysisType type)
    {
        this.type = type;
    }

    public abstract void extractSolvingInfo(Circuit circuit);

    public abstract IAnalysisResults run();
}
