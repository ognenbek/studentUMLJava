package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DiagramModel.java
//Class DiagramModel is a Model component of the MVC architecture
//It stores the graphical representation of UML concepts that are part of a UML diagram.
//This class is abstract with known subclasses including SSD/SD/CCD/DCDModel.
//It extends Observable to notify the views of any changes that occur.
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.UMLProject;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Vector;

import org.w3c.dom.Element;

public abstract class DiagramModel extends Observable implements Serializable, IXMLCustomStreamable {

    public static final int UCD = 0;
    public static final int SSD = 1;
    public static final int SD = 2;
    public static final int CCD = 3;
    public static final int DCD = 4;
    public static final int AD = 5;
    
    protected String diagramName;
    protected DiagramInternalFrame frame;
    protected NotifierVector graphicalElements;
    protected Vector<GraphicalElement> selected;

    // every diagram has to have a reference to the central repository of UML elements
    protected CentralRepository repository;
    protected UMLProject umlProject;

    public DiagramModel() {
    }

    public DiagramModel(String name, UMLProject umlp) {
        diagramName = name;
        graphicalElements = new NotifierVector();
        selected = null;
        umlProject = umlp;
        repository = umlp.getCentralRepository();
        umlProject.addDiagram(this);
        selected = new Vector<GraphicalElement>();
    }

    public void setFrame(DiagramInternalFrame frm) {
        frame = frm;
    }

    public DiagramInternalFrame getFrame() {
        return frame;
    }

    // This method adds a graphical element to the diagram, triggered by the controller.
    // The default behavior is to simply add the graphical element to the list
    // and notify the view observers, but subclasses may override this behavior,
    // for example in case the addition of one element affects other elements
    public void addGraphicalElement(GraphicalElement e) {
        e.objectAdded(e);
        graphicalElements.add(e);
        modelChanged();
    }

    public void insertGraphicalElementAt(GraphicalElement e, int index) {
        e.objectAdded(e);
        graphicalElements.insertElementAt(e, index);
        modelChanged();
    }

    // This method removes a graphical element from the diagram, triggered by the controller.
    // The default behavior is to simply remove the graphical element from the list, if
    // it exists, and notify the observers, but subclasses may override this behavior,
    // for example in case the deletion of one element (e.g. design class) triggers the
    // deletion of other elements (associations, dependencies, etc.)
    public void removeGraphicalElement(GraphicalElement e) {
        e.objectRemoved(e);
        graphicalElements.remove(e);
        modelChanged();
    }

    // This method moves a graphical element in the drawing area, by changing its coordinates.
    // The method is usually triggered by a drag event caused by the drag-and-drop controller.
    // Each element responds polymorphically with its move() method.
    // The simplest behavior is implemented by calling the element's move method, but
    // subclasses may override this behavior in case the movement of one element affects
    // other elements
    public void moveGraphicalElement(GraphicalElement e, int x, int y) {
        e.move(x, y);
        modelChanged();
    }

    // This method moves a graphical element in the drawing area, but in difference from
    // moveGraphicalElement(), it is triggered by a releasing of the mouse button after
    // dragging, in case the moved element may have to be validated in its new position.
    // The simplest behavior is exactly the same as move(), but subclasses may override it.
    public void settleGraphicalElement(GraphicalElement e, int x, int y) {
        moveGraphicalElement(e, x, y);
    }

    // This method causes the selection of a single element at a time in the diagram
    public void selectGraphicalElement(GraphicalElement el) {

        int i = graphicalElements.indexOf(el);

        if (i != -1) {
            GraphicalElement e = (GraphicalElement) graphicalElements.get(i);
            selected.add(e);
            e.setSelected(true);
            modelChanged();
            //SystemWideObjectNamePool.getInstance().reload();
        }
    }

    public void clearSelected() {
        Iterator iterator = graphicalElements.iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = (GraphicalElement) iterator.next();
            element.setSelected(false);
        }

        selected.clear();
        modelChanged();
        //SystemWideObjectNamePool.getInstance().reload();
    }

    // retrieves the currently selected graphical element, if any
    public Vector<GraphicalElement> getSelectedGraphicalElements() {
        return selected;
    }

    // retrieves the graphical element of a particular index, null if doesn't exist
    public GraphicalElement getGraphicalElement(int index) {
        GraphicalElement ge = null;

        try {
            ge = (GraphicalElement) graphicalElements.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return ge;
    }

    public NotifierVector getGraphicalElements() {
        return graphicalElements;
    }

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public String getDiagramName() {
        return diagramName;
    }

    public void setDiagramName(String name) {
        diagramName = name;
    }

    // retrieves the graphical element in the diagram that contains a given 2D point
    // Usually triggered by a select, drag-and-drop, and addition event.
    public GraphicalElement getContainingGraphicalElement(Point2D point) {

        // get the first element that contains the point, starting from the end of the list,
        // i.e. from the most recently drawn grapical element, so that the uppermost is
        // returned in case elements are overlayed one on top of the other
        ListIterator listIterator = graphicalElements.listIterator(graphicalElements.size());
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if (element.contains(point)) {
                return element;
            }
        }

        // if no element was found, return null
        return null;
    }

    public GraphicalElement getContainingGraphicalElement(int x, int y) {
        return this.getContainingGraphicalElement(new Point2D.Double(x, y));
    }

    // clears the drawing area of a diagram by setting all graphical elements to empty
    public void clear() {
        while (graphicalElements.size() > 0) {
            removeGraphicalElement((GraphicalElement) graphicalElements.get(0));
        }
        graphicalElements.clear();
        graphicalElements = new NotifierVector();
        modelChanged();
    }

    public void setName(String name) {
        diagramName = name;
        modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // method that prints the diagram's name
    public String toString() {
        return diagramName;
    }

    // this custom method is called whenever a change in the diagram occurs to notify observers
    public void modelChanged() {

        setChanged();
        notifyObservers();

    }

    public void setRect(String rect) {
        if (rect != null) {
            java.lang.System.out.println("RECT : " + rect);
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getDiagramName());
        if (frame != null) {
            String values = frame.getBounds().x + "," + frame.getBounds().y + "," + frame.getBounds().width + "," + frame.getBounds().height;
            node.setAttribute("framex", values);
            node.setAttribute("selected", Boolean.toString(frame.isSelected()));
            node.setAttribute("iconified", Boolean.toString(frame.isIcon()));
        }
        streamer.streamObjects(node, graphicalElements.iterator());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setDiagramName(node.getAttribute("name"));

        graphicalElements.clear();
        streamer.streamObjectsFrom(node, graphicalElements, instance);
    }

    public UMLProject getUmlProject() {
        return umlProject;
    }
}
