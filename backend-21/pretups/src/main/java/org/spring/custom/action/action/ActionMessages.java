/*
 * $Id: ActionMessages.java 54929 2004-10-16 16:38:42Z germuska $
 *
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spring.custom.action.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

/**
 * <p>A class that encapsulates messages. Messages can be either global
 * or they are specific to a particular bean property.</p>
 *
 * <p>Each individual message is described by an <code>ActionMessage</code>
 * object, which contains a message key (to be looked up in an appropriate
 * message resources database), and up to four placeholder arguments used for
 * parametric substitution in the resulting message.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - It is assumed that these objects
 * are created and manipulated only within the context of a single thread.
 * Therefore, no synchronization is required for access to internal
 * collections.</p>
 *
 * @version $Rev: 54929 $ $Date: 2004-10-17 01:38:42 +0900 (日, 17 10月 2004) $
 * @since Struts 1.1
 */
public class ActionMessages implements Serializable {


    /**
     * <p>Compares ActionMessageItem objects.</p>
     */
    private static final Comparator actionItemComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((ActionMessageItem) o1).getOrder()
                    - ((ActionMessageItem) o2).getOrder();
        }
    };


    // ----------------------------------------------------- Manifest Constants


    /**
     * <p>The "property name" marker to use for global messages, as opposed to
     * those related to a specific property.</p>
     */
    public static final String GLOBAL_MESSAGE =
            "org.apache.struts.action.GLOBAL_MESSAGE";


    // ----------------------------------------------------- Instance Variables


    /**
     * <p>Have the messages been retrieved from this object?</p>
     *
     * <p>The controller uses this property to determine if session-scoped
     * messages can be removed.</p>
     *
     * @since Struts 1.2
     */
    protected boolean accessed = false;


    /**
     * <p>The accumulated set of <code>ActionMessage</code> objects (represented
     * as an ArrayList) for each property, keyed by property name.</p>
     */
    protected HashMap messages = new HashMap();


    /**
     * <p>The current number of the property/key being added. This is used
     * to maintain the order messages are added.</p>
     */
    protected int iCount = 0;


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Create an empty <code>ActionMessages</code> object.</p>
     */
    public ActionMessages() {

        super();

    }


    /**
     * <p>Create an <code>ActionMessages</code> object initialized with the given
     * messages.</p>
     *
     * @param messages The messages to be initially added to this object.
     * This parameter can be <code>null</code>.
     * @since Struts 1.1
     */
    public ActionMessages(ActionMessages messages) {
        super();
        this.add(messages);
    }


    /**
     * <p>Add a message to the set of messages for the specified property. An
     * order of the property/key is maintained based on the initial addition
     * of the property/key.</p>
     *
     * @param property  Property name (or ActionMessages.GLOBAL_MESSAGE)
     * @param message   The message to be added
     */
    public void add(String property, ActionMessage message) {

        ActionMessageItem item = (ActionMessageItem) messages.get(property);
        List list = null;

        if (item == null) {
            list = new ArrayList();
            item = new ActionMessageItem(list, iCount++, property);

            messages.put(property, item);
        } else {
            list = item.getList();
        }

        list.add(message);

    }


    /**
     * <p>Adds the messages from the given <code>ActionMessages</code> object to
     * this set of messages. The messages are added in the order they are returned from
     * the <code>properties</code> method. If a message's property is already in the current
     * <code>ActionMessages</code> object, it is added to the end of the list for that
     * property. If a message's property is not in the current list it is added to the end
     * of the properties.</p>
     *
     * @param messages The <code>ActionMessages</code> object to be added.  
     * This parameter can be <code>null</code>.
     * @since Struts 1.1
     */
    public void add(ActionMessages messages) {

        if (messages == null) {
            return;
        }

        // loop over properties
        Iterator props = messages.properties();
        while (props.hasNext()) {
            String property = (String) props.next();

            // loop over messages for each property
            Iterator msgs = messages.get(property);
            while (msgs.hasNext()) {
                ActionMessage msg = (ActionMessage) msgs.next();
                this.add(property, msg);
            }
        }
    }


    /**
     * <p>Clear all messages recorded by this object.</p>
     */
    public void clear() {

        messages.clear();

    }


    /**
     * <p>Return <code>true</code> if there are no messages recorded
     * in this collection, or <code>false</code> otherwise.</p>
     *
     * @since Struts 1.1
     */
    public boolean isEmpty(){

        return (messages.isEmpty());


    }


    /**
     * <p>Return the set of all recorded messages, without distinction
     * by which property the messages are associated with. If there are
     * no messages recorded, an empty enumeration is returned.</p>
     */
    public Iterator get() {

        this.accessed = true;

        if (messages.isEmpty()) {
            return Collections.EMPTY_LIST.iterator();
        }

        ArrayList results = new ArrayList();
        ArrayList actionItems = new ArrayList();

        for (Iterator i = messages.values().iterator(); i.hasNext();) {
            actionItems.add(i.next());
        }

        // Sort ActionMessageItems based on the initial order the
        // property/key was added to ActionMessages.
        Collections.sort(actionItems, actionItemComparator);

        for (Iterator i = actionItems.iterator(); i.hasNext();) {
            ActionMessageItem ami = (ActionMessageItem) i.next();

            for (Iterator messages = ami.getList().iterator(); messages.hasNext();) {
                results.add(messages.next());
            }
        }

        return results.iterator();
    }


    /**
     * <p>Return the set of messages related to a specific property.
     * If there are no such messages, an empty enumeration is returned.</p>
     *
     * @param property Property name (or ActionMessages.GLOBAL_MESSAGE)
     */
    public Iterator get(String property) {

        this.accessed = true;

        ActionMessageItem item = (ActionMessageItem) messages.get(property);

        if (item == null) {
            return (Collections.EMPTY_LIST.iterator());
        } else {
            return (item.getList().iterator());
        }

    }


    /**
     * <p>Returns <code>true</code> if the <code>get()</code> or
     * <code>get(String)</code> methods are called.</p>
     *
     * @return <code>true</code> if the messages have been accessed one or more
     * times.
     * @since Struts 1.2
     */
    public boolean isAccessed() {

        return this.accessed;

    }


    /**
     * <p>Return the set of property names for which at least one message has
     * been recorded. If there are no messages, an empty <code>Iterator</code> is returned.
     * If you have recorded global messages, the <code>String</code> value of
     * <code>ActionMessages.GLOBAL_MESSAGE</code> will be one of the returned
     * property names.</p>
     */
    public Iterator properties() {

        if (messages.isEmpty()) {
            return Collections.EMPTY_LIST.iterator();
        }

        ArrayList results = new ArrayList();
        ArrayList actionItems = new ArrayList();

        for (Iterator i = messages.values().iterator(); i.hasNext();) {
            actionItems.add(i.next());
        }

        // Sort ActionMessageItems based on the initial order the
        // property/key was added to ActionMessages.
        Collections.sort(actionItems, actionItemComparator);

        for (Iterator i = actionItems.iterator(); i.hasNext();) {
            ActionMessageItem ami = (ActionMessageItem) i.next();
            results.add(ami.getProperty());
        }

        return results.iterator();

    }


    /**
     * <p>Return the number of messages recorded for all properties (including
     * global messages). <strong>NOTE</strong> - it is more efficient to call
     * <code>isEmpty</code> if all you care about is whether or not there are
     * any messages at all.</p>
     */
    public int size() {

        int total = 0;

        for (Iterator i = messages.values().iterator(); i.hasNext();) {
            ActionMessageItem ami = (ActionMessageItem) i.next();
            total += ami.getList().size();
        }

        return (total);

    }


    /**
     * <p>Return the number of messages associated with the specified property.</p>
     *
     * @param property Property name (or ActionMessages.GLOBAL_MESSAGE)
     */
    public int size(String property) {

        ActionMessageItem item = (ActionMessageItem) messages.get(property);

        return (item == null) ? 0 : item.getList().size();
    }


    /**
     * <p>Returns a String representation of this ActionMessages'
     * property name=message list mapping.</p>
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.messages.toString();
    }


    /**
     * <p>This class is used to store a set of messages associated with a
     * property/key and the position it was initially added to list.</p>
     */
    protected class ActionMessageItem implements Serializable {


        /**
         * <p>The list of <code>ActionMessage</code>s.</p>
         */
        protected List list = null;


        /**
         * <p>The position in the list of messages.</p>
         */
        protected int iOrder = 0;


        /**
         * <p>The property associated with <code>ActionMessage</code>.</p>
         */
        protected String property = null;


        public ActionMessageItem(List list, int iOrder, String property) {
            this.list = list;
            this.iOrder = iOrder;
            this.property = property;
        }


        public List getList() {
            return list;
        }


        public void setList(List list) {
            this.list = list;
        }


        public int getOrder() {
            return iOrder;
        }


        public void setOrder(int iOrder) {
            this.iOrder = iOrder;
        }


        public String getProperty() {
            return property;
        }


        public void setProperty(String property) {
            this.property = property;
        }


        public String toString() {
            return this.list.toString();
        }

    }

}