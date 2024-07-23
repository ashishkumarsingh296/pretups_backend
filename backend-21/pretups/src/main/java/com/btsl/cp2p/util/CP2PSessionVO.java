/*
 * Created on Sep 16, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.cp2p.util;

import java.io.Serializable;

/**
 * @author abhijit.chauhan
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CP2PSessionVO implements Serializable {
    private int counter = 0;
    private Object list;
    private static final long serialVersionUID = 1L;
  
    /**
     * @return
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @return
     */
    public Object getList() {
        return list;
    }

    /**
     * @param i
     */
    public void setCounter(int i) {
        counter = i;
    }

    /**
     * @param list
     */
    public void setList(Object list) {
        this.list = list;
    }

    public synchronized void incrementCounter() {
        counter++;
    }

    public synchronized void decrementCounter() {
        counter--;
    }
}
