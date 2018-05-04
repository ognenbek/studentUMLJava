package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddCallMessageController.java
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddCallMessageController extends AddElementController {

    private RoleClassifierGR source = null;
    private Vector elements;

    public AddCallMessageController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddCallMessageController(SSDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof RoleClassifierGR) && element.contains(origin)) {
                source = (RoleClassifierGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (source == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            // a call message goes to normal objects as well as multiobjects
            if (((element instanceof SDObjectGR) || (element instanceof MultiObjectGR) || element instanceof SystemInstanceGR) && element.contains(origin)) {
                addCallMessage(source, (RoleClassifierGR) element, y);

                break;
            }
        }

        // set originating role classifier to null to start over again
        source = null;
    }

    public void addCallMessage(RoleClassifierGR source, RoleClassifierGR target, int y) {
        CallMessage message = new CallMessage(source.getRoleClassifier(), target.getRoleClassifier(),
                new GenericOperation(""));
        CallMessageGR messageGR = new CallMessageGR(source, target, message, y);

        UndoableEdit edit = new AddEdit(messageGR, diagramModel);

        // handle the rest of addition details to the diagram model
        diagramModel.addGraphicalElement(messageGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}
