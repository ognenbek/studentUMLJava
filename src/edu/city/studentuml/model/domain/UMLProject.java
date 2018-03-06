package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
//import edu.city.studentuml.applet.Application;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.w3c.dom.Element;

public class UMLProject extends Observable implements Serializable, Observer, IXMLCustomStreamable {

    private static UMLProject ref = null;
    private NotifierVector diagramModels;
    private CentralRepository repository;
    private Boolean projectSaved = true;
    private String user;
    //for applet
    private int status;
    private int exid;
    private int parentid;
    private String nodeType;
    private String title;
    private String comment;
    private Mode mode;
    //for desktop application
    private String projectFilename = "";
    private String projectFilepath = "";
    private String projectName = "";

    protected UMLProject() {
        ref = this;
        projectInit();
    }

    protected UMLProject(String Filepath, String filename) {
        projectInit();
    }

    private void projectInit() {
        repository = new CentralRepository();
        diagramModels = new NotifierVector();
        repository.addObserver(this);

        //applet
        title = "";
        comment = "";
        status = 0;
        nodeType = "";

        //application
        projectName = "New Project";
    }

    public static UMLProject getInstance() {
        if (ref == null) {
            ref = new UMLProject();
        }
        return ref;
    }

    public void clear() {
        //ApplicationGUI.closeFrames();
        diagramModels.clear();
        repository.clear();
        SystemWideObjectNamePool.getInstance().clear();
        projectChanged();
    }

    public void becomeObserver() {
        DiagramModel model;
        Iterator iterator = diagramModels.iterator();

        while (iterator.hasNext()) {
            model = (DiagramModel) iterator.next();
            model.addObserver(this);
        }

        repository.addObserver(this);
    }

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public DiagramModel getDiagramModel(int index) {
        return (DiagramModel) diagramModels.elementAt(index);
    }

    public Vector getDiagramModels() {
        return diagramModels;
    }

    public Boolean isSaved() {
        return projectSaved;
    }

    public void setSaved(Boolean saved) {
        projectSaved = saved;
        setChanged();
        notifyObservers();
    }

    public void addDiagram(DiagramModel dm) {
        diagramModels.add(dm);
        dm.addObserver(this);
        setSaved(false);
    }

    public void removeDiagram(DiagramModel dm) {
        diagramModels.remove(dm);
        setSaved(false);
    }

    public void projectChanged() {
        setChanged();
        notifyObservers();
    }

    public void update(Observable observable, Object object) {
        projectChanged();
    }

    public void loadFromXML(String filename) {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFile(filename);

        Element e = streamer.getNodeById(null, "project");
        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        streamer.finishedParsing();

        projectChanged();
    }
    // Embed4Auto

    public void loadFromURL(String url) {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadURL(url);

        Element e = streamer.getNodeById(null, "project");

        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        streamer.finishedParsing();

        projectChanged();
    }

    //for undo/redo
    public void loadFromXMLString(String xmlString) {

        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFromString(xmlString);

        Element e = streamer.getNodeById(null, "project");
        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();
        streamer.finishedParsing();

        projectChanged();
    }

    public void streamToXML(String path) {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, "project", this);

        if (ApplicationGUI.isApplet()) {
            streamer.saveToURL(path);
        } else {
            streamer.saveToFile(path);
        }
    }

    //for undo/redo
    public String streamToXMLString() {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, "project", this);
        return streamer.streamToString();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        diagramModels.clear();
        streamer.streamObjectsFrom(node, diagramModels, instance);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        streamer.streamObjects(node, diagramModels.iterator());
    }

    public boolean isClassReferenced(GraphicalElement el, AbstractClass abstractClass) {
        if (abstractClass == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof CCDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ConceptualClassGR) {
                        if (currEl != el && ((ConceptualClassGR) currEl).getConceptualClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }

            if (dm instanceof DCDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ClassGR) {
                        if (currEl != el && ((ClassGR) currEl).getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }

            if (dm instanceof SDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof SDObjectGR) {
                        if (currEl != el && ((SDObjectGR) currEl).getSDObject().getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                    if (currEl instanceof MultiObjectGR) {
                        if (currEl != el && ((MultiObjectGR) currEl).getMultiObject().getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isActorReferenced(GraphicalElement el, Actor actor) {
        if (actor == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof AbstractSDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ActorInstanceGR) {
                        if (currEl != el && ((ActorInstanceGR) currEl).getActorInstance().getActor() == actor) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isSystemReferenced(GraphicalElement el, System system) {
        if (system == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof SSDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof SystemInstanceGR) {
                        if (currEl != el && ((SystemInstanceGR) currEl).getSystemInstance().getSystem() == system) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setExid(int exid) {
        this.exid = exid;
    }

    public int getExid() {
        return exid;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getParentid() {
        return parentid;
    }

    // application
    public String getName() {
        return projectName;
    }

    public void setName(String name) {
        projectName = name;
        setSaved(false);
    }

    public String getFilename() {
        return projectFilename;
    }

    public void setFilename(String filename) {
        projectFilename = filename;
        setSaved(false);
    }

    public String getFilepath() {
        return projectFilepath;
    }

    public void setFilepath(String filepath) {
        projectFilepath = filepath;
        setSaved(false);
    }
}
