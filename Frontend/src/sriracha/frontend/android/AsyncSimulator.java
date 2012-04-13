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
                boolean success = false;
                ReadyCallback callback;

                synchronized (simLock)
                {
                    busy = true;
                    try
                    {
                        success = simulator.setNetlist(netlist);
                        callback = new ReadyCallback(callbackHandler, success);
                    }
                    catch (Exception e)
                    {
                        callback = new ReadyCallback(callbackHandler, e);
                    }
                    finally
                    {
                        busy = false;
                    }
                }

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
                boolean success = false;
                DoneCallback callback;

                synchronized (simLock)
                {
                    busy = true;
                    try
                    {
                        success = simulator.addAnalysis(analysis);
                        callback = new DoneCallback(callbackHandler, success);
                    }
                    catch (Exception e)
                    {
                        callback = new DoneCallback(callbackHandler, e);
                    }
                    finally
                    {
                        busy = false;
                    }
                }

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

        /**
         * called if bad things happened
         */
        public void OnSimulatorError(Exception e);
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

        /**
         * called if bad things happened
         */
        public void OnSimulatorError(Exception e);
    }

    private static class ReadyCallback extends Thread
    {
        private OnSimulatorReadyListener callbackHandler;
        private boolean success;
        private Exception e;

        private ReadyCallback(OnSimulatorReadyListener callbackHandler, boolean success)
        {
            this.callbackHandler = callbackHandler;
            this.success = success;
        }

        private ReadyCallback(OnSimulatorReadyListener callbackHandler, Exception e)
        {
            this.callbackHandler = callbackHandler;
            this.e = e;
        }

        @Override
        public void run()
        {
            if (e != null)
            {
                callbackHandler.OnSimulatorError(e);
            }
            else if (success)
            {
                callbackHandler.OnSimulatorReady();
            }
            else
            {
                callbackHandler.OnSimulatorSetupCancelled();
            }
        }
    }

    private static class DoneCallback extends Thread
    {
        private OnSimulatorAnalysisDoneListener callbackHandler;
        private boolean success;
        private Exception e;

        private DoneCallback(OnSimulatorAnalysisDoneListener callbackHandler, boolean success)
        {
            this.callbackHandler = callbackHandler;
            this.success = success;
        }

        private DoneCallback(OnSimulatorAnalysisDoneListener callbackHandler, Exception e)
        {
            this.callbackHandler = callbackHandler;
            this.e = e;
        }

        @Override
        public void run()
        {
            if (e != null)
            {
                callbackHandler.OnSimulatorError(e);
            }
            else if (success)
            {
                callbackHandler.OnSimulatorAnalysisDone();
            }
            else
            {
                callbackHandler.OnSimulatorAnalysisCancelled();
            }
        }
    }
}
