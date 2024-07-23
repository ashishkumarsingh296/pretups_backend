package com.selftopup.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.comparators.ComparatorChain;

/**
 * @(#)ListSorterUtil.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Mohit Goel 05/06/2005 Initial Creation
 * 
 *                         This class is used for sorting
 * 
 */
public class ListSorterUtil {
    public static final String ASCENDING = "ascending";
    public static final String DESCENDING = "descending";
    private Comparator defaultBeanComparator;
    private Boolean defaultComparatorInitialized = Boolean.FALSE;

    /**
     * Builds the Comparator
     * Uses ComparatorChain for the chain sorting
     */
    private Comparator buildComparator(String sortby, String order) {
        ComparatorChain comparatorChain = null;

        if ((sortby != null) && (!sortby.equals(""))) {
            StringTokenizer stringTokenizer = new StringTokenizer(sortby, ",");
            comparatorChain = new ComparatorChain();
            int i = 0;
            BTSLBeanComparator beanComparator = null;
            while (stringTokenizer.hasMoreElements()) {
                String property = stringTokenizer.nextToken();
                beanComparator = new BTSLBeanComparator(property);
                comparatorChain.addComparator(beanComparator);
                comparatorChain.setForwardSort(i);
                i++;
            }
        }

        if ((order != null) && (!order.equals(""))) {
            StringTokenizer stringTokenizer = new StringTokenizer(order, ",");
            int i = 0;
            while (stringTokenizer.hasMoreElements()) {
                String property = stringTokenizer.nextToken();
                if (property != null && property.equals(DESCENDING))
                    comparatorChain.setReverseSort(i);
                else
                    comparatorChain.setForwardSort(i);
                i++;
            }
        }

        return comparatorChain;
    }

    /**
     * Returns the beanComparator
     */
    private Comparator getDefaultBeanComparator(String sortBy, String order) {
        if (!defaultComparatorInitialized.booleanValue()) {
            synchronized (defaultComparatorInitialized) {
                if (!defaultComparatorInitialized.booleanValue()) {
                    defaultBeanComparator = buildComparator(sortBy, order);
                    defaultComparatorInitialized = Boolean.TRUE;
                }
            }
        }

        return defaultBeanComparator;
    }

    /**
     * Does multi column Sorting
     * 
     * @param sortBy
     *            ("," seperated property name of the Object; Do not add space
     *            after comma)
     * @param order
     *            ("," seperated order: Ascending or Descending order; Do not
     *            add space after comma); <li>If null, then Default is ascending
     *            </li>
     * @param result
     *            (Collection object)
     * @return Collection Sorted result
     */
    public Collection doSort(String sortBy, String order, Collection result) throws Exception {
        if (result != null && result.size() > 0) {
            Comparator comparator = getDefaultBeanComparator(sortBy, order);
            Collections.sort((List) result, comparator);
        }
        return result;
    }
}
