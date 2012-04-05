package sriracha.frontend.android.model.elements.sources;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.WireManager;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.model.CircuitElement;

public class GroundView extends CircuitElementView {
    CircuitElementPortView ports[];

    public GroundView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager) {
        super(context, element, positionX, positionY, wireManager);
    }

    @Override
    public int getDrawableId() {
        return R.drawable.sources_ground;
    }

    @Override
    public CircuitElementPortView[] getElementPorts() {
        if (ports == null) {
            ports = new CircuitElementPortView[]{
                    new CircuitElementPortView(this, 0, -0.5f),
            };
        }
        return ports;
    }
}
