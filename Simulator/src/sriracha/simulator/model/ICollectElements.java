package sriracha.simulator.model;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICollectElements {

    public void addElement(CircuitElement e);

    /**
     * Add new node mapping
     *
     * @param nodeName - name of node from netlist
     * @return index for node
     */
    public int assignNodeMapping(String nodeName);
}
