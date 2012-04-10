package sriracha.frontend.android;

import android.app.Activity;
import sriracha.simulator.IPrintData;
import sriracha.simulator.ISimulator;
import sriracha.simulator.Simulator;

public class AsyncSimulator
{
    private ISimulator simulator;

    Activity mainActivity;

    private boolean busy;

    private Object simLock = new Object();

    public AsyncSimulator(Activity mainActivity)
    {
        this.mainActivity = mainActivity;
        simulator = Simulator.Instance;
    }


    public void setNetlistAsync(final String netlist, final OnSimulatorReadyListener callbackHandler)
    {
        new Thread()
        {
            public void run()
            {
                synchronized (simLock)
                {
                    busy = true;
                    simulator.setNetlist(netlist);
                    busy = false;
                }

                Thread callback = new Thread()
                {
                    public void run()
                    {
                        callbackHandler.OnSimulatorReady();
                    }

                };

                mainActivity.runOnUiThread(callback);

            }
        }.start();
    }


    public void requestAnalysisAsync(final String analysis, final OnSimulatorAnalysisDoneListener callbackHandler)
    {
        new Thread()
        {
            public void run()
            {
                synchronized (simLock)
                {
                    busy = true;
                    simulator.addAnalysis(analysis);
                    busy = false;
                }

                Thread callback = new Thread()
                {
                    public void run()
                    {
                        callbackHandler.OnSimulatorAnalysisDone();
                    }

                };

                mainActivity.runOnUiThread(callback);


            }
        }.start();
    }

    /**
     * this method should be called in the OnSimulatorAnalysisDoneListener callback
     *
     * @param printStatement Netlist .PRINT statement
     * @return filtered analysis data.
     */
    public IPrintData requestResults(final String printStatement)
    {
        return simulator.requestPrintData(printStatement);
    }

    public boolean isBusy()
    {
        return busy;
    }

    public interface OnSimulatorReadyListener
    {
        public void OnSimulatorReady();
    }

    public interface OnSimulatorAnalysisDoneListener
    {
        public void OnSimulatorAnalysisDone();
    }
}
