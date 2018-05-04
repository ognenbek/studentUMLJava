/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.SDMessage;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractSDModel extends DiagramModel {

    // minimum distance that can be kept between messages
    public static final int MINIMUM_MESSAGE_DISTANCE = 30;
    // minimum distance that can be kept between role classifiers
    public static final int MINIMUM_RC_DISTANCE = 60;
    // clone list of role classifiers and messages that is maintained for
    // consistency purposes, ordering, etc.
    protected Vector roleClassifiers;
    protected Vector messages;

    public AbstractSDModel(String title, UMLProject umlp) {
        super(title, umlp);
        roleClassifiers = new NotifierVector();
        messages = new NotifierVector();
    }

    // override superclass method addGraphicalElement
    // The element type is determined and the appropriate add method is called.
    public final void addGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof RoleClassifierGR) {
            addRoleClassifier((RoleClassifierGR) e);
        } else if (e instanceof SDMessageGR) {
            addMessage((SDMessageGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.addGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    // before calling the superclass add element, the role classifier
    // is added to the project repository, then to the roleClassifiers list,
    // which is sorted and finally x positions are decided to keep the minimum distances
    public final void addRoleClassifier(RoleClassifierGR rc) {
        if (rc instanceof ActorInstanceGR) {
            repository.addActorInstance(((ActorInstanceGR) rc).getActorInstance());
        }
        addToRepository(rc);    //subclasses can add other role classifiers to repository

        roleClassifiers.add(rc);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
        super.addGraphicalElement(rc);
    }

    // hook; calls the methods in subclass that need to perform subclass specific tasks
    protected void addToRepository(RoleClassifierGR rc) {
    }

    // before calling the superclass add element, the message
    // is added to the project repository, then to the messages list,
    // which is validated for consistency,
    // then sorted, ranked, and y positions are decided for minimum distances
    public final void addMessage(SDMessageGR m) {
        repository.addSDMessage(m.getMessage());
        messages.add(m);
        super.addGraphicalElement(m);
        validateMessages();
        // sort the messages, give them ranks, and keep the distances
        messagesChanged();
        restoreMessagesDistances();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // override superclass method moveGraphicalElement
    // to handle cases when the movement of one element affects other
    // elements, rather than simply moving alone
    public final void moveGraphicalElement(GraphicalElement e, int x, int y) {
        if (e instanceof RoleClassifierGR) {
            moveRoleClassifier((RoleClassifierGR) e, x, y);
        } else if (e instanceof SDMessageGR) {
            moveMessage((SDMessageGR) e, x, y);
        } else { //UML Notes
            super.moveGraphicalElement(e, x, y);
        }
    }

    public final void moveRoleClassifier(RoleClassifierGR rc, int x, int y) {
        super.moveGraphicalElement(rc, x, y);
        roleClassifiersChanged();
    }

    public final void moveMessage(SDMessageGR m, int x, int y) {
        super.moveGraphicalElement(m, x, y);
        messagesChanged();
    }

    // override superclass method settleGraphicalElement
    // apart from just moving the dragged and dropped element
    // this method also rearrangeds the elements to keep the distance
    public final void settleGraphicalElement(GraphicalElement e, int x, int y) {
        if (e instanceof RoleClassifierGR) {
            settleRoleClassifier((RoleClassifierGR) e, x, y);
        } else if (e instanceof SDMessageGR) {
            settleMessage((SDMessageGR) e, x, y);
        }
    }

    public final void settleRoleClassifier(RoleClassifierGR rc, int x, int y) {
        super.moveGraphicalElement(rc, x, y);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
    }

    public final void settleMessage(SDMessageGR m, int x, int y) {
        super.moveGraphicalElement(m, x, y);
        validateMessages();
        // sort the messages, give them ranks, and keep the distances
        messagesChanged();
        restoreMessagesDistances();
    }

    // subclasses that need to validate create and destroy messages need to override this method
    public void validateMessages() {
    }

    // called whenever role classifeirs change, by resorting the list
    // and updating the lifeline lengths
    public final void roleClassifiersChanged() {
        sortRoleClassifiers();
        updateLifelineLengths();
    }

    // called whenever messages change, by resorting the list and putting ranks,
    // and updating the lifeline lengths
    public final void messagesChanged() {
        sortMessages();
        updateLifelineLengths();
    }

    // sort the role classifiers list according to their x position
    protected void sortRoleClassifiers() {
        // use two-pass bubblesort
        RoleClassifierGR object1;
        RoleClassifierGR object2;

        for (int pass = 1; pass < roleClassifiers.size(); pass++) {
            for (int element = 0; element < roleClassifiers.size() - 1; element++) {
                object1 = (RoleClassifierGR) roleClassifiers.elementAt(element);
                object2 = (RoleClassifierGR) roleClassifiers.elementAt(element + 1);

                if (object1.getX() > object2.getX()) {
                    swap(roleClassifiers, element, element + 1);
                }
            }
        }
    }

    // sort the messages list according to their y position
    protected void sortMessages() {
        // use two-pass bubblesort
        SDMessageGR message1;
        SDMessageGR message2;

        for (int pass = 1; pass < messages.size(); pass++) {
            for (int element = 0; element < (messages.size() - 1); element++) {
                message1 = (SDMessageGR) messages.elementAt(element);
                message2 = (SDMessageGR) messages.elementAt(element + 1);

                if (message1.getY() > message2.getY()) {
                    swap(messages, element, element + 1);
                }
            }
        }

        SDMessage m;

        // reset the messages numbering according to their rank
        for (int i = 0; i < messages.size(); i++) {
            message1 = (SDMessageGR) messages.elementAt(i);
            m = message1.getMessage();
            m.setRank(i + 1);
        }
    }

    // utility method needed by bubble sorting
    private void swap(Vector elements, int index1, int index2) {
        Object tempObject = elements.elementAt(index1);

        elements.setElementAt(elements.elementAt(index2), index1);
        elements.setElementAt(tempObject, index2);
    }

    // keeps the same ordering of role classifiers, but may change their x positions
    // in order to keep the minimum distance between them
    protected void restoreRoleClassifiersDistances() {
        RoleClassifierGR object1;
        RoleClassifierGR object2;

        for (int i = 0; i < roleClassifiers.size() - 1; i++) {
            object1 = (RoleClassifierGR) roleClassifiers.elementAt(i);
            object2 = (RoleClassifierGR) roleClassifiers.elementAt(i + 1);

            if (object2.getX() - object1.getX() < MINIMUM_RC_DISTANCE) {
                object2.move(object1.getX() + MINIMUM_RC_DISTANCE, 0);
            }
        }
    }

    // keeps the same ordering of messages, but may change their y positions
    // in order to keep the minimum distances between them
    protected void restoreMessagesDistances() {
        SDMessageGR message1;
        SDMessageGR message2;

        for (int i = 0; i < messages.size() - 1; i++) {
            message1 = (SDMessageGR) messages.elementAt(i);
            message2 = (SDMessageGR) messages.elementAt(i + 1);

            if (message2.getY() - message1.getY() < MINIMUM_MESSAGE_DISTANCE) {
                message2.move(0, message1.getY() + MINIMUM_MESSAGE_DISTANCE);
            }
        }

        updateLifelineLengths();
    }

    // retrieves the list of all role classifiers
    public Vector getRoleClassifiers() {
        return roleClassifiers;
    }

    // retrieves the list of all messages
    public Vector getMessages() {
        return messages;
    }

    // updates the lifeline lengths of role classifiers whenever
    // a message is added or moved
    public final void updateLifelineLengths() {
        if (messages.size() != 0) {
            int highestMessageY = ((SDMessageGR) messages.lastElement()).getY();

            // extend the life lines if any message is too down the drawing view
            setEndingY(highestMessageY + RoleClassifierGR.MINIMUM_LIFELINE_LENGTH);
        } else {
            setEndingY(RoleClassifierGR.VERTICAL_OFFSET + RoleClassifierGR.MINIMUM_LIFELINE_LENGTH);
        }
    }

    // returns true if the argument object has been destroyed
    public boolean isDestroyed(RoleClassifierGR rc) {
        Iterator iterator = messages.iterator();
        SDMessageGR message;

        while (iterator.hasNext()) {
            message = (SDMessageGR) iterator.next();

            if ((message instanceof DestroyMessageGR) && (message.getTarget() == rc)) {
                return true;
            }
        }

        return false;
    }

    // this changes the lifeline lengths of all the role classifiers,
    // except those that have been destroyed (i.e. have a determined lifeline length
    public final void setEndingY(int y) {
        Vector objects = getRoleClassifiers();
        Iterator iterator = objects.iterator();
        RoleClassifierGR object;

        while (iterator.hasNext()) {
            object = (RoleClassifierGR) iterator.next();

            if (!isDestroyed(object)) {
                object.setEndingY(y);
            }
        }
    }

    public final void clear() {
        while (graphicalElements.size() > 0) {
            removeGraphicalElement((GraphicalElement) graphicalElements.get(0));
        }

        super.clear();
    }

    // override superclass method removeGraphicalElement
    public final void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof RoleClassifierGR) {
            removeRoleClassifier((RoleClassifierGR) e);
        } else if (e instanceof SDMessageGR) {
            removeMessage((SDMessageGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    public final void removeRoleClassifier(RoleClassifierGR rc) {
        // Remove messages associated with the RoleClassifier
        Iterator iterator = messages.iterator();
        SDMessageGR message;

        while (iterator.hasNext()) {
            message = (SDMessageGR) iterator.next();

            if ((message.getSource() == rc) || (message.getTarget() == rc)) {
                removeMessage(message);

                // refresh the iterator
                iterator = messages.iterator();
            }
        }


        removeFromRepository(rc);

        roleClassifiers.remove(rc);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
        super.removeGraphicalElement(rc);
    }

    // removes role classifiers from the repository
    protected final void removeFromRepository(RoleClassifierGR rc) {
        if (rc.getRoleClassifier() instanceof ActorInstance) {
            if (!umlProject.isActorReferenced(rc, ((ActorInstance) rc.getRoleClassifier()).getActor())) {
                repository.removeActor(((ActorInstance) rc.getRoleClassifier()).getActor());

            }
            repository.removeActorInstance(((ActorInstance) rc.getRoleClassifier()));
        }

        removeClassifiersFromRepository(rc);
    }

    // hook: remove other classifiers than actor instance from the repository
    protected void removeClassifiersFromRepository(RoleClassifierGR rc) {
    }

    // method template
    public final void removeMessage(SDMessageGR e) {
        if (e instanceof CallMessageGR) {
            removeCallMessage((CallMessageGR) e);
        } else if (e instanceof ReturnMessageGR) {
            removeReturnMessage((ReturnMessageGR) e);
        }
        removeOtherMessages(e); //hook for subclasses

        messagesChanged();
        restoreMessagesDistances();
        super.removeGraphicalElement(e);
    }

    // hook
    public void removeOtherMessages(SDMessageGR e) {
    }

    public void removeCallMessage(CallMessageGR callMessage) {
        callMessage.getCallMessage().clear();
        repository.removeSDMessage(callMessage.getMessage());
        messages.remove(callMessage);
    }

    public void removeReturnMessage(ReturnMessageGR returnMessage) {
        repository.removeSDMessage(returnMessage.getMessage());
        messages.remove(returnMessage);
    }
}
