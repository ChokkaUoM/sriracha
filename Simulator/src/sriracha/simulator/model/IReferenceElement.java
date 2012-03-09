package sriracha.simulator.model;

/**
 * Some circuit elements are defined with respect to another element,
 * This introduces issues if such elements are included in subcircuit.
 * The subcircuit system relies on a element independent cloning system, However these elements will need to refer to
 * the element in their own
 * since elements need a generic cloning mechanism
 * (For example: CCCS and CCVS reference a dummy voltage source used to define the branch current
 * they are dependent on)
 */
public interface IReferenceElement
{
    public void setReferencedElement(CircuitElement referencedElement);
}
