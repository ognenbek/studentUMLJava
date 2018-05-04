/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.frame;

import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.ConsistencyChecker;
import edu.city.studentuml.view.gui.CollectionTreeModel;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * @author ogo_b
 */
public class testClass {
    protected UMLProject umlProject = null;
    protected String currentRuleFile;
    protected String simpleRulesFile;
    protected String advancedRulesFile;
    protected Vector rules = new Vector();
    HashMap<Object, String> map = new HashMap<Object, String>();
    public testClass() {
        create();
    }
    public void create(){
        createUMLProject();
        simpleRulesFile = getResource(Constants.RULES_DIR + "simplerules.txt");
        advancedRulesFile = getResource(Constants.RULES_DIR + "advancedrules.txt");
        currentRuleFile = advancedRulesFile;
        SystemWideObjectNamePool.getInstance().init(currentRuleFile);
        CCDModel ccd = new CCDModel("model1",umlProject);
        
        ConceptualClassGR graphicalClass = new ConceptualClassGR(new ConceptualClass(""), new Point(0, 0));
        graphicalClass.getAbstractClass().setName("andreas");
        ccd.addGraphicalElement(graphicalClass);
        
        ConceptualClassGR newClass = new ConceptualClassGR(new ConceptualClass(""), new Point(0, 0));
        ccd.addGraphicalElement(newClass);
        
        map.put(ccd, "0");
        map.put(graphicalClass, "1");
        map.put(newClass, "2");

        SystemWideObjectNamePool.getInstance().objectAdded(ccd);
        SystemWideObjectNamePool.getInstance().objectAdded(graphicalClass);
        SystemWideObjectNamePool.getInstance().objectAdded(newClass);
        checkState();
        
    }
    private String getResource(String path) {
        return this.getClass().getResource(path).toString();
    }
    
    private void checkState(){
        SystemWideObjectNamePool.getInstance().setRuleFile(currentRuleFile);
        /*SystemWideObjectNamePool.getInstance().reload();
        SystemWideObjectNamePool.getInstance().reloadRules();
        SystemWideObjectNamePool.getInstance().generateRuleSetPublic(map);*/
        System.out.println("messages: " + SystemWideObjectNamePool.getInstance().getMess());

        
    }
    
    private void createUMLProject() {
        umlProject = UMLProject.getInstance();
        umlProject.becomeObserver();
        SystemWideObjectNamePool.umlProject = umlProject;
    }
    
}
