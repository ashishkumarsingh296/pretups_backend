package com.btsl.pretups.channel.queue;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

/*
 * @(#)DataQueue.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gaurav Pandey 01/01/2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2014 Mahindra Comviva Ltd.
 */
public class DataQueue {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static int maxQueueSize = Integer.valueOf(Constants.getProperty("QUEUE_POOL_SIZE"));
    private int queueCounter = 0;

    private PriorityBlockingQueue<ThreadPoolClient> elements = new PriorityBlockingQueue<ThreadPoolClient>(maxQueueSize, (Comparator<? super ThreadPoolClient>) idComparator);

    public static Comparator<ThreadPoolClient> idComparator = new Comparator<ThreadPoolClient>() {
        public int compare(ThreadPoolClient o1, ThreadPoolClient o2) {
            final Integer priority1 = o1.getRequestQueueVO().getPriority();
            final Integer priority2 = o2.getRequestQueueVO().getPriority();
            return priority1.compareTo(priority2);
        }
    };

    public synchronized final void enqueue(ThreadPoolClient newElement) {
        elements.add(newElement);
        queueCounter++;
        notify();
    }

    public synchronized final Runnable dequeue() {
        try {
            queueCounter--;
        } catch (Exception ex) {
            _log.error("Error in getting the object from the queue", ex);
        }
        return elements.poll();
    }

    public synchronized final boolean remove(Runnable object) {
        try {
            queueCounter--;
        } catch (Exception ex) {
            _log.error("Error in getting the object from the queue", ex);
        }
        return elements.remove(object);
    }

    public synchronized final boolean isEmpty() {
        return elements.isEmpty();

    }

    public synchronized final boolean isFull() {
        return !(queueCounter < maxQueueSize);
    }

    public synchronized final Iterator<ThreadPoolClient> getIterator() {

        final Iterator<ThreadPoolClient> iterator = elements.iterator();
        return iterator;
    }

}
