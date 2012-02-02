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
        private DataOutputStream dataOut;

        public solverThread(PipedInputStream inputStream, IAnalysis analysis) {
            this.inputStream = inputStream;
            this.analysis = analysis;
            eqClone = equation.clone();
            setupStream();
        }


        private void setupStream() {
            try {
                dataOut = new DataOutputStream(new PipedOutputStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

         private void flush(IComplexVector solution, double omega) {
             try {
                    dataOut.writeDouble(omega);
                 for (int i = 0; i < solution.getDimension(); i++) {
                     dataOut.writeDouble(solution.getValue(i).getReal());
                 }
             } catch (IOException e) {
                 e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
             }
         }

        private void ssLogScaleRun(int base, SmallSignal freq){
            double f = freq.getfStart();
            int i=0, k = 1;
            while(f <=freq.getfEnd()){
                double interval = (f * k) * (base - 1) / freq.getPoints();
                while(i < freq.getPoints() && f <= freq.getfEnd()) {
                    f = f + i*interval;
                    flush(eqClone.solve(f), f);
                    i++;
                }
                k*=base;
                i=0;
            }
        }

        @Override
        public void run() {

            //todo: implement analysis types
            if(analysis instanceof SmallSignal){
                SmallSignal freq = (SmallSignal) analysis;
                switch (freq.getType()){
                    case Linear:
                        double interval = (freq.getfEnd() - freq.getfStart())/freq.getPoints();
                        for(int i = 0; i< freq.getPoints(); i++ ){
                            double omega = freq.getfStart() + interval*i;
                            flush(eqClone.solve(omega), omega);
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


            try {
                dataOut.flush();
                dataOut.close();
                dataOut = null;
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

    }

    public PipedInputStream solve(IAnalysis analysis) {
        PipedInputStream in = new PipedInputStream();
        solverThread t = new solverThread(in, analysis);
        t.start();
        return in;
    }
}
