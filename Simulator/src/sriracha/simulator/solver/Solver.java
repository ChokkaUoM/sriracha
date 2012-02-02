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

         private void flush(IComplexVector solution) {
             try {
                 for (int i = 0; i < solution.getDimension(); i++) {
                     dataOut.writeDouble(solution.getValue(i).getReal());
                 }
             } catch (IOException e) {
                 e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                            flush(eqClone.solve(freq.getfStart() + interval*i));
                        }
                        break;
                    case Decade:

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
