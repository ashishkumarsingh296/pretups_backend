package com.selftopup.common;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @(#)BTSLBeanComparator.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Mohit Goel 05/06/2005 Initial Creation
 * 
 *                             This class is used for sorting
 * 
 */
public class BTSLBeanComparator extends BeanComparator implements Comparator, Serializable {

    public BTSLBeanComparator(String property) {
        super(property);
    }

    /**
     * Compare two JavaBeans by their shared property.
     * If {@link #getProperty} is null then the actual objects will be compared.
     * 
     * @param o1
     *            Object The first bean to get data from to compare against
     * @param o2
     *            Object The second bean to get data from to compare
     * @return int negative or positive based on order
     */
    public int compare(Object o1, Object o2) {
        String property = super.getProperty();
        Comparator comparator = super.getComparator();

        if (getProperty() == null) {
            // compare the actual objects
            return comparator.compare(o1, o2);
        }

        Object value1 = null;
        Object value2 = null;

        try {
            if (o1 != null) {
                value1 = PropertyUtils.getProperty(o1, property);
            }

            if (o2 != null) {
                value2 = PropertyUtils.getProperty(o2, property);
            }
            if (value1 == null && value2 == null)
                return 0;
            else if (value1 == null)
                return -1;
            else if (value2 == null)
                return 1;
            else
                return comparator.compare(value1, value2);
        } catch (Exception e) {
            throw new ClassCastException(e.toString());
        }
    }
}
