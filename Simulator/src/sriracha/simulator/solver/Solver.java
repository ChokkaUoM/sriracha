package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.interfaces.IAnalysis;
import sriracha.simulator.solver.interfaces.IEquation;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Solver {

    private IEquation equation;

    public Solver(IEquation equation) {
        this.equation = equation;
    }

    private class solverThread extends Thread {

        private IEquation eqClone;
        private PipedInputStream inputStream;
        private IAnalysis analysis;
        private AnalysisResults results;
        private OutputFilter filter;
        private boolean saveResults;
        private DataOutputStream dataOut;


        public solverThread(PipedInputStream inputStream, IAnalysis analysis, OutputFilter filter) {
            this.inputStream = inputStream;
            this.analysis = analysis;
            this.filter = filter;
            this.saveResults = false;
            eqClone = equation.clone();
            setupStream();
        }

        public solverThread(IAnalysis analysis, AnalysisResults results) {
            this.analysis = analysis;
            this.results = results;
            this.saveResults = true;
            eqClone = equation.clone();
        }


        private void setupStream() {
            try {
                dataOut = new DataOutputStream(new PipedOutputStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }



        private void ssLogScaleRun(int base, SmallSignal freq){
            double f = freq.getfStart();
            int i=0, k = 1;
            while(f <=freq.getfEnd()){
                double interval = (f * k) * (base - 1) / freq.getPoints();
                while(i < freq.getPoints() && f <= freq.getfEnd()) {
                    f = f + i*interval;
                    IComplexVector soln = eqClone.solve(f);
                    if(!saveResults){
                        filter.flush(dataOut, soln, f);
                    }else {
                        results.addVector(f, soln);
                    }
                    i++;
                }
                k*=base;
                i=0;
            }
        }

        
        @Override
        public void run() {

            if(analysis instanceof SmallSignal){
                SmallSignal freq = (SmallSignal) analysis;
                switch (freq.getSSType()){
                    case Linear:
                        double interval = (freq.getfEnd() - freq.getfStart())/freq.getPoints();
                        for(int i = 0; i< freq.getPoints(); i++ ){
                            double omega = freq.getfStart() + interval*i;
                            IComplexVector soln = eqClone.solve(omega);
                            if(!saveResults){
                                filter.flush(dataOut, soln, omega);
                            }else {
                                results.addVector(omega, soln);
                            }
                        }
                        break;
                    case Decade:
                        ssLogScaleRun(10, freq);
                        break;
                    case Octave:
                        ssLogScaleRun(8, freq);
                        break;

                }
            }

            if(!saveResults){
                try {
                    dataOut.flush();
                    dataOut.close();
                    dataOut = null;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }

    public PipedInputStream solve(IAnalysis analysis, OutputFilter filter) {
        PipedInputStream in = new PipedInputStream();
        solverThread t = new solverThread(in, analysis, filter);
        t.start();
        return in;
    }

    public AnalysisResults getResults(IAnalysis analysis) {
        AnalysisResults results;
        switch (analysis.getType()){
            case AC:
                results = new SSResults();
                break;
            default:
                throw new UnsupportedOperationException("analysis type not supported");
        }
        solverThread t = new solverThread(analysis, results);
        t.run();//yes this is deliberate we do not want to return before the solving is done.
        return results;
    }
}
