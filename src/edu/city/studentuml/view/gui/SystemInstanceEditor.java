package edu.city.studentuml.view.gui;

//Author: Dragan Bisercic
//ActorInstanceEditor.java
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class SystemInstanceEditor extends JPanel implements ActionListener, ItemListener {

    private JPanel systemPanel;
    private JLabel systemLabel;
    private JComboBox systemComboBox;
    private JDialog systemInstanceDialog;

    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;

    private final JPanel cardPanel;
    private final JPanel emptyPanel;
    private final JPanel nonemptyPanel;
    private final JLabel editSystemLabel;

    private JPanel centerPanel;
    private JButton editSystemButton;

    private JPanel bottomPanel;
    private JButton okButton;
    private JButton cancelButton;

    private boolean ok;

    private System system;
    private Vector systems;
    private SystemInstanceGR systemInstance;
    private CentralRepository repository;

    public SystemInstanceEditor(SystemInstanceGR s, CentralRepository cr) {
        systemInstance = s;
        repository = cr;
        systems = (Vector) repository.getSystems().clone();
        setLayout(new BorderLayout());

        centerPanel = new JPanel(new GridLayout(3, 1));

        namePanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel("System Instance Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        systemPanel = new JPanel(new FlowLayout());
        systemLabel = new JLabel("System: ");
        systemComboBox = new JComboBox();
        systemComboBox.setMaximumRowCount(5);
        systemComboBox.addItemListener(this);
        systemPanel.add(systemLabel);
        systemPanel.add(systemComboBox);

        cardPanel = new JPanel(new CardLayout());
        emptyPanel = new JPanel();
        nonemptyPanel = new JPanel(new FlowLayout());
        editSystemLabel = new JLabel("Add new system: ");
        editSystemButton = new JButton("Add...");
        editSystemButton.addActionListener(this);
        nonemptyPanel.add(editSystemLabel);
        nonemptyPanel.add(editSystemButton);
        cardPanel.add("empty", emptyPanel);
        cardPanel.add("nonempty", nonemptyPanel);

        centerPanel.add(namePanel);
        centerPanel.add(systemPanel);
        centerPanel.add(cardPanel);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(20);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        initialize();
    }

    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        systemInstanceDialog = new JDialog(owner, true);
        systemInstanceDialog.getContentPane().add(this);
        systemInstanceDialog.setTitle(title);
        systemInstanceDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        systemInstanceDialog.pack();
        systemInstanceDialog.setResizable(false);
        systemInstanceDialog.setLocationRelativeTo(owner);
        systemInstanceDialog.show();

        return ok;
    }

    public void initialize() {
        SystemInstance instance = systemInstance.getSystemInstance();
        system = instance.getSystem();

        nameField.setText(instance.getName());

        // initialize the system names combo box
        if (!isInList(system, systems)) {
            systems.add(0, system);
        }

        System s;
        Iterator iterator = systems.iterator();
        while (iterator.hasNext()) {
            s = (System) iterator.next();

            if ((s != null) && !s.getName().equals("")) {
                systemComboBox.addItem(s.getName());
            } else {
                systemComboBox.addItem("(unnamed)");
            }
        }

        systemComboBox.setSelectedIndex(systems.indexOf(system));
        updateEditSystemPanel();
    }

    public boolean isInList(System system, Vector list) {
        Iterator iterator = list.iterator();
        System s;

        while (iterator.hasNext()) {
            s = (System) iterator.next();

            if (s == system) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return nameField.getText();
    }

    public System getSystem() {
        return (System) systems.elementAt(systemComboBox.getSelectedIndex());
    }

    // edits the given system
    public void editSystem(System s) {
        // show the system editor dialog
        String systemName = JOptionPane.showInputDialog("Enter the System's Name");

        if (systemName == null) {    // user has pressed cancel
            return;
        }

        // ensure that the to-be-edited system exists in the repository
        repository.addSystem(s);

        System newSystem = new System(systemName);

        // edit the system if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!s.getName().equals(newSystem.getName())
                && (repository.getSystem(newSystem.getName()) != null)
                && !newSystem.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing system with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
        } else {
            repository.editSystem(s, newSystem);
        }
    }

    // updates the combo box according to the list of systems
    public void updateComboBox() {
        int selectedIndex = systemComboBox.getSelectedIndex();

        systemComboBox.removeAllItems();

        System s;
        Iterator iterator = systems.iterator();
        while (iterator.hasNext()) {
            s = (System) iterator.next();

            if ((s != null) && !s.getName().equals("")) {
                systemComboBox.addItem(s.getName());
            } else {
                systemComboBox.addItem("(unnamed)");
            }
        }

        systemComboBox.setSelectedIndex(selectedIndex);
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            systemInstanceDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            systemInstanceDialog.setVisible(false);
        } else if (event.getSource() == editSystemButton) {
            editSystem((System) systems.elementAt(systemComboBox.getSelectedIndex()));
            updateComboBox();
            updateEditSystemPanel();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        updateEditSystemPanel();
    }

    private void updateEditSystemPanel() {
        System a = systemInstance.getSystemInstance().getSystem();
        String s = getSelectedItem();

        if (a.getName().equals("")) {
            if (s.equals("(unnamed)")) {
                setPanel("nonempty");
            } else {
                setPanel("empty");
            }
        } else {
            setPanel("empty");
        }
    }

    private void setPanel(String s) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, s);
    }

    private String getSelectedItem() {
        String s = (String) systemComboBox.getSelectedItem();
        if (s == null) {
            return "";
        } else {
            return (String) systemComboBox.getSelectedItem();
        }
    }
}
