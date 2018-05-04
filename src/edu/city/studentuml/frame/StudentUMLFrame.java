package edu.city.studentuml.frame;

import edu.city.studentuml.applet.StudentUMLApplet;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.ApplicationFrame;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.view.gui.CheckTreeManager;
import edu.city.studentuml.view.gui.RepositoryTreeView;
import java.awt.EventQueue;
import java.awt.event.KeyListener;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JTree;



/**
 *
 * @author draganbisercic
 */
public class StudentUMLFrame extends JFrame {
     protected static boolean isApplet = false;
    protected StudentUMLFrame frame = null;
    protected StudentUMLApplet applet = null;
    protected boolean repairMode = true;
    protected UMLProject umlProject = null;
    protected CentralRepository centralRepository;
    protected String simpleRulesFile;
    protected String advancedRulesFile;
    protected String currentRuleFile;
    protected static final String rulesLocation = "/edu/city/studentuml/util/validation/rules/";
    protected RepositoryTreeView repositoryTreeView;
    protected JTree factsTree;
    protected JTree messageTree;
    protected CheckTreeManager checkTreeManager;
        protected int openFrameCounter = 0;
    public static String DESKTOP_USER = "Desktop Application User";
    private static ApplicationGUI instance; // need in ObjectFactory [backward compatiblity]
        protected int ruleEditorTabPlacement = -1;
    protected int factsTreeTabPlacement = -1;

    private static StudentUMLFrame instance1 = null;

    private StudentUMLFrame() {
        super("StudentUML");
    }

    public static StudentUMLFrame getInstance() {
        if (instance1 == null) {
            instance1 = new StudentUMLFrame();
        }

        return instance1;
    }
    public static void main(String args[]) {
        StudentUMLFrame f = StudentUMLFrame.getInstance();
        ApplicationGUI applicationGUI = new ApplicationFrame(f);
        
        //testClass test = new testClass();

    }
}
