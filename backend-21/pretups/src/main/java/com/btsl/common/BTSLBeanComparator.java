package com.btsl.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

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
 * @author ayush.abhijeet
 */
public class BTSLBeanComparator extends BeanComparator implements Comparator, Serializable {
    private transient static final Log _log = LogFactory.getLog(BTSLBeanComparator.class.getName());

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
    @Override
    public int compare(Object o1, Object o2) {
        final String METHOD_NAME = "compare";
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

            return gettingo2Property(o2, property, comparator, value1, value2);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new ClassCastException(e.toString());
        }
    }

	private int gettingo2Property(Object o2, String property,
			Comparator comparator, Object value1, Object value2)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (o2 != null) {
		    value2 = PropertyUtils.getProperty(o2, property);
		}
		if (value1 == null && value2 == null) {
		    return 0;
		} else if (value1 == null) {
		    return -1;
		} else if (value2 == null) {
		    return 1;
		} else {
		    return comparator.compare(value1, value2);
		}
	}
}
