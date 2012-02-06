import sriracha.simulator.model.Capacitor;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.Resistor;
import sriracha.simulator.model.VoltageSource;
import sriracha.simulator.solver.EquationGenerator;
import sriracha.simulator.solver.SSType;
import sriracha.simulator.solver.SmallSignal;
import sriracha.simulator.solver.Solver;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Eventually this should be an interactive test console for playing around with various parts of the simulator.
 */
public class Console {

    public static void main(String[] args) throws IOException {
          testSequence1();
    }



    public static void testSequence1() throws IOException {
        Circuit test1 = new Circuit(1);
        test1.elements.add(new VoltageSource(0, -1, 1));
        test1.elements.add(new Resistor(0, -1, 2));
        test1.elements.add(new Capacitor(0, -1, 2));

        EquationGenerator generator = new EquationGenerator(test1);

        Solver solver = new Solver(generator.generate());

        SmallSignal analysis = new SmallSignal(SSType.Linear, )


        DataInputStream dataStream = new DataInputStream(solver.solve(analysis));
        try {
            while (true){

                System.out.print(dataStream.readDouble() + " ");

            }
        }catch (EOFException e){
            dataStream.close();
        }

         
        


    }
}
