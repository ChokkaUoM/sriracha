package sriracha.simulator;

/**
 * Options class,
 * Holds various options for the simulator,
 * currently only use is debugging options.
 */
public class Options
{
    static boolean PrintMatrix;

    static boolean PrintCircuit;

    static boolean PrintProgress;

    public static boolean isPrintProgress()
    {
        return PrintProgress;
    }

    public static void setPrintProgress(boolean printProgress)
    {
        PrintProgress = printProgress;
    }

    public static boolean isPrintMatrix()
    {
        return PrintMatrix;
    }

    public static void setPrintMatrix(boolean printMatrix)
    {
        PrintMatrix = printMatrix;
    }

    public static boolean isPrintCircuit()
    {
        return PrintCircuit;
    }

    public static void setPrintCircuit(boolean printCircuit)
    {
        PrintCircuit = printCircuit;
    }
}
