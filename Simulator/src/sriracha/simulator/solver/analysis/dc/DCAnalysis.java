package sriracha.simulator.solver.analysis.dc;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;


public class DCAnalysis extends Analysis
{
    private DCEquation equation;

    protected DCAnalysis()
    {
        super(AnalysisType.DC);
    }

    @Override
    public void extractEquation(Circuit circuit)
    {
        circuit.assignAdditionalVarIndices();
        equation = DCEquation.generate(circuit);
    }

    @Override
    public IAnalysisResults run()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
