package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UCLinkGR;
import javax.swing.undo.AbstractUndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class RemoveEditFactory {

    private static RemoveEditFactory ref = null;

    private RemoveEditFactory() {
    }

    public static RemoveEditFactory getInstance() {
        if (ref == null) {
            ref = new RemoveEditFactory();
        }
        return ref;
    }

    public AbstractUndoableEdit createRemoveEdit(GraphicalElement element, DiagramModel model) {
        if (model instanceof ADModel) {
            if (element instanceof NodeComponentGR) {
                return new NodeRemoveEdit((NodeComponentGR) element, (ADModel) model);
            } else if (element instanceof EdgeGR) {
                return new EdgeRemoveEdit((EdgeGR) element, (ADModel) model);
            } else {
                throw new UnsupportedOperationException("Error in creating remove edit");
            }
        } else if (model instanceof UCDModel) {
            if (element instanceof UCDComponentGR) {
                return new UCDElementRemoveEdit((UCDComponentGR) element, (UCDModel) model);
            } else if (element instanceof UCLinkGR) {
                return new UCLinkRemoveEdit((UCLinkGR) element, (UCDModel) model);
            } else {
                throw new UnsupportedOperationException("Error in creating remove edit");
            }
        }

        return null;
    }
}
