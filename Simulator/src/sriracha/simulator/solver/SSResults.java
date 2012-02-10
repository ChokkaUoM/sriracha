package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplexVector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSResults extends AnalysisResults{

    private ArrayList<SSResult> data;

    public SSResults() {
        data = new ArrayList<SSResult>();
    }

    private class outputThread extends Thread {

        private OutputFilter filter;
        private DataOutputStream dataOut;

        public outputThread(PipedInputStream inputStream, OutputFilter filter) {
            this.filter = filter;

            try {
                dataOut = new DataOutputStream(new PipedOutputStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        @Override
        public void run() {
            for(SSResult r : data){
                filter.flush(dataOut, r.vector, r.w);    
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

    @Override
    public void addVector(double w, IComplexVector vector){
        data.add(new SSResult(w, vector));
    }

    @Override
    public PipedInputStream output(OutputFilter filter){
        PipedInputStream in = new PipedInputStream();
        outputThread t = new outputThread(in, filter);
        t.start();
        return in;
        
    }
}

class SSResult{
    double w;
    IComplexVector vector;

    SSResult(double w, IComplexVector vector) {
        this.w = w;
        this.vector = vector;
    }
}
