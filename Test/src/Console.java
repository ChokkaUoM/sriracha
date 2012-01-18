import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CurrentSource;
import sriracha.simulator.model.Resistor;
import sriracha.simulator.solver.EquationGenerator;
import sriracha.simulator.solver.Solver;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class Console {

    public static void main(String[] args) throws IOException {
          testSequence1();
    }



    public static void testSequence1() throws IOException {
        Circuit test1 = new Circuit(1);
        test1.elements.add(new CurrentSource(0, -1, 1));
        test1.elements.add(new Resistor(0, -1, 1));

        EquationGenerator generator = new EquationGenerator(test1);

        Solver solver = new Solver(generator.generate());

        DataInputStream dataStream = new DataInputStream(solver.solve(null));
        try {
            while (true){

                System.out.print(dataStream.readDouble() + " ");

            }
        }catch (EOFException e){
            dataStream.close();
        }

         
        


    }
}
