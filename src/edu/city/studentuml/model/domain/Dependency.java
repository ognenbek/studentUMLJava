package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Dependency.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class Dependency implements Serializable, IXMLCustomStreamable {

    private DesignClass from;
    private DesignClass to;

    public Dependency(DesignClass a, DesignClass b) {
        from = a;
        to = b;
    }

    public DesignClass getFrom() {
        return from;
    }

    public DesignClass getTo() {
        return to;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub

        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(from));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
    }
}
