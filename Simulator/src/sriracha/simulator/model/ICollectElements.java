package sriracha.simulator.model;

/**
 * interface for classes that hold collections of elements
 * and maintain internal string to node index mappings
 */
public interface ICollectElements {

    /**
     * add a new element to the collection.
     * the elements in a collection must all have unique names.
     * @param e the new element
     */
    public void addElement(CircuitElement e);

    /**
     * Add new node mapping or return existing one if already mapped
     *
     * @param nodeName - name of node from netlist
     * @return index for node
     */
    public int assignNodeMapping(String nodeName);
}
