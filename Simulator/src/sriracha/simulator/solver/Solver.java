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


        @Override
        public void run() {

            //todo: implment analysis types

            IComplexVector solution = eqClone.solve();
            try {
                for (int i = 0; i < solution.getDimension(); i++) {

                    dataOut.writeDouble(solution.getValue(i).getReal());

                }
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
