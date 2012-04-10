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
                final boolean success;

                synchronized (simLock)
                {
                    busy = true;
                    success = simulator.setNetlist(netlist);
                    busy = false;
                }

                Thread callback = new Thread()
                {
                    public void run()
                    {
                        if (success)
                        {
                            callbackHandler.OnSimulatorReady();
                        } else
                        {
                            callbackHandler.OnSimulatorSetupCancelled();
                        }

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
                final boolean success;

                synchronized (simLock)
                {
                    busy = true;
                    success = simulator.addAnalysis(analysis);
                    busy = false;
                }

                Thread callback = new Thread()
                {
                    public void run()
                    {
                        if (success)
                        {
                            callbackHandler.OnSimulatorAnalysisDone();
                        } else
                        {
                            callbackHandler.OnSimulatorAnalysisCancelled();
                        }
                    }

                };

                mainActivity.runOnUiThread(callback);


            }
        }.start();
    }

    public void cancelAnalysis()
    {
        simulator.requestCancel();
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
        /**
         * called once the netlist has been parsed and any included analyses
         * have been run
         */
        public void OnSimulatorReady();

        /**
         * Called if the setNetlist call was cancelled.
         * will not happen if no analysis was requested inside the original
         * netlist
         */
        public void OnSimulatorSetupCancelled();
    }

    public interface OnSimulatorAnalysisDoneListener
    {
        /**
         * called when analysis is finished
         */
        public void OnSimulatorAnalysisDone();

        /**
         * called if analysis was finished early
         */
        public void OnSimulatorAnalysisCancelled();
    }
}
