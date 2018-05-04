package edu.city.studentuml.view;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DiagramView.java
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;

public abstract class DiagramView extends JPanel implements Observer {

    protected DiagramModel model;
    protected Line2D dragLine = new Line2D.Double(0, 0, 0, 0);

    public DiagramView(DiagramModel m) {
        model = m;

        if (m != null) {
            m.addObserver(this);
        }

        setBackground(Color.white);
        setSize(500, 400);
        setDoubleBuffered(true);
    }
    
    public Line2D getDragLine() {
        return dragLine;
    }

    public void setDragLine(Line2D dragLine) {
        this.dragLine = dragLine;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawDiagram(g2d);
    }

    // Original size image
    public BufferedImage getImage() {
        return getImageByScale(1, 1);
    }

    // Image of a specified width in pixels, maintaining aspect ratio
    public BufferedImage getImageByWidth(int width) {
        double scalex = (double) width / getWidth();

        return getImageByScale(scalex, scalex);
    }

    // Image of a specified height in pixels, maintaining aspect ratio
    public BufferedImage getImageByHeight(int height) {
        double scaley = (double) height / getHeight();

        return getImageByScale(scaley, scaley);
    }

    // Image of a specified width and height in pixels
    // without necessarily maintaining aspect ratio
    public BufferedImage getImageByDimensions(int width, int height) {
        double scalex = (double) width / getWidth();
        double scaley = (double) height / getHeight();

        return getImageByScale(scalex, scaley);
    }

    // Image scaled by a factors of "scalex" and "scaley"
    // to maintain aspect ratio, scalex = scaley
    public BufferedImage getImageByScale(double scalex, double scaley) {

        int imageWidth = (int) (getWidth() * scalex);
        int imageHeight = (int) (getHeight() * scaley);

        // create a new image with the (scaled) dimensions of the view panel
        BufferedImage image = new BufferedImage(
                imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        // retrieve the graphics context of the image
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setBackground(Color.white);
        g.clearRect(0, 0, imageWidth, imageHeight);

        // maintain aspect ratio by using the same scale for x and y
        g.scale(scalex, scaley);

        // call method drawDiagram to draw the uml elements on the image
        drawDiagram(g);

        return image;
    }

    public void drawDiagram(Graphics2D g) {

        //IJ:make a little Z-order first draw the Links then everything on top of it...
        SystemWideObjectNamePool.drawLock.lock();

        Iterator iterator = model.getGraphicalElements().iterator();
        GraphicalElement element;

        Vector<GraphicalElement> others = new Vector();
        while (iterator.hasNext()) { //TODO here to pass number of links??
            element = (GraphicalElement) iterator.next();
            if ((element instanceof LinkGR)) {
                element.draw(g);
            } else {
                others.add(element);
            }
        }
        for (int x = 0; x < others.size(); x++) {
            others.get(x).draw(g);
        }

        g.setPaint(Color.GRAY);
        g.draw(dragLine);

        SystemWideObjectNamePool.drawLock.unlock();
    }

    public void setModel(DiagramModel m) {
        if (model != null) {
            model.deleteObserver(this);
        }

        model = m;

        if (m != null) {
            m.addObserver(this);
            repaint();
        }
    }

    public DiagramModel getModel() {
        return model;
    }

    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public void update(Observable observable, Object object) {
        repaint();
    }
}
