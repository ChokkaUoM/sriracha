package sriracha.frontend.android;

import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.elements.*;
import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;
import sriracha.frontend.model.elements.ctlsources.*;
import sriracha.frontend.model.elements.sources.*;

import java.util.*;

/**
 * Static class used to map circuit elements to their respective UUIDs.
 * Each type of circuit element has a UUID that is used in the serialization
 * process as a placeholder for that element, since the {@link CircuitElementView}s
 * themselves cannot be easily serialized. Deserialization involves looking up
 * the appropriate type based on the UUID and instantiating it reflectively.
 */
public class ElementTypeUUID
{
    public static final HashMap<UUID, Class<? extends CircuitElementView>> VIEW_MAP = new HashMap<UUID, Class<? extends CircuitElementView>>();
    public static final HashMap<UUID, Class<? extends CircuitElement>> ELEMENT_MAP = new HashMap<UUID, Class<? extends CircuitElement>>();

    public static final UUID VOLTAGE_SOURCE				= UUID.fromString("ec7d5559-0163-41f8-aec1-2db1047286a8");
    public static final UUID CURRENT_SOURCE				= UUID.fromString("f05d1bc9-ed0c-4944-b91e-8404c298a374");
    public static final UUID DEPENDENT_VOLTAGE_SOURCE	= UUID.fromString("8d148dbe-fcd4-4927-88bb-a1ebaca6a25e");
    public static final UUID DEPENDENT_CURRENT_SOURCE	= UUID.fromString("cd36e545-67d6-4ff1-86ab-7dbd5dd4aefa");
    public static final UUID VCC						= UUID.fromString("38a63260-8841-4d67-be54-14bbb68e3eb6");
    public static final UUID GROUND						= UUID.fromString("907fef61-4422-4915-a74c-8b02ca46c69e");
    public static final UUID RESISTOR					= UUID.fromString("24d7d51b-dd44-44d4-b138-9cdcba67c064");
    public static final UUID CAPACITOR					= UUID.fromString("bff2976e-c1b7-4c4b-a684-417492ba7ed4");
    public static final UUID INDUCTOR					= UUID.fromString("58fa0479-3fc2-45b4-8921-e2a57d0364c1");

    static {
        VIEW_MAP.put(VOLTAGE_SOURCE, VoltageSourceView.class);
        VIEW_MAP.put(CURRENT_SOURCE, CurrentSourceView.class);
        VIEW_MAP.put(DEPENDENT_VOLTAGE_SOURCE, DependentVoltageSourceView.class);
        VIEW_MAP.put(DEPENDENT_CURRENT_SOURCE, DependentCurrentSourceView.class);
        VIEW_MAP.put(VCC, VCCView.class);
        VIEW_MAP.put(GROUND, GroundView.class);
        VIEW_MAP.put(RESISTOR, ResistorView.class);
        VIEW_MAP.put(CAPACITOR, CapacitorView.class);
        VIEW_MAP.put(INDUCTOR, InductorView.class);

        ELEMENT_MAP.put(VOLTAGE_SOURCE, VoltageSource.class);
        ELEMENT_MAP.put(CURRENT_SOURCE, CurrentSource.class);
        ELEMENT_MAP.put(DEPENDENT_VOLTAGE_SOURCE, DependentVoltageSource.class);
        ELEMENT_MAP.put(DEPENDENT_CURRENT_SOURCE, DependentCurrentSource.class);
        ELEMENT_MAP.put(VCC, VCC.class);
        ELEMENT_MAP.put(GROUND, Ground.class);
        ELEMENT_MAP.put(RESISTOR, Resistor.class);
        ELEMENT_MAP.put(CAPACITOR, Capacitor.class);
        ELEMENT_MAP.put(INDUCTOR, Inductor.class);
    }
}
