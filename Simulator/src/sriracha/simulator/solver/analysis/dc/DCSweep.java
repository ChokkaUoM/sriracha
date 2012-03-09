package sriracha.simulator.solver.analysis.dc;


public class DCSweep
{
    private String srcName;

    private double vStart, vEnd, vStep;


    public DCSweep(String srcName, double vStart, double vEnd, double vStep)
    {
        this.srcName = srcName;
        this.vStart = vStart;
        this.vEnd = vEnd;
        this.vStep = vStep;
    }


    public String getSrcName()
    {
        return srcName;
    }

    public double getvStart()
    {
        return vStart;
    }

    public double getvEnd()
    {
        return vEnd;
    }

    public double getvStep()
    {
        return vStep;
    }
}
