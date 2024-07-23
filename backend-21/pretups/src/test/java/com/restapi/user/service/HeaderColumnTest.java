package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HeaderColumnTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link HeaderColumn#HeaderColumn()}
     *   <li>{@link HeaderColumn#setColumnName(String)}
     *   <li>{@link HeaderColumn#setDisplayName(String)}
     *   <li>{@link HeaderColumn#getColumnName()}
     *   <li>{@link HeaderColumn#getDisplayName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        HeaderColumn actualHeaderColumn = new HeaderColumn();
        actualHeaderColumn.setColumnName("Column Name");
        actualHeaderColumn.setDisplayName("Display Name");
        assertEquals("Column Name", actualHeaderColumn.getColumnName());
        assertEquals("Display Name", actualHeaderColumn.getDisplayName());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link HeaderColumn#HeaderColumn(String, String)}
     *   <li>{@link HeaderColumn#setColumnName(String)}
     *   <li>{@link HeaderColumn#setDisplayName(String)}
     *   <li>{@link HeaderColumn#getColumnName()}
     *   <li>{@link HeaderColumn#getDisplayName()}
     * </ul>
     */
    @Test
    public void testConstructor2() {
        HeaderColumn actualHeaderColumn = new HeaderColumn("Column Name", "Display Name");
        actualHeaderColumn.setColumnName("Column Name");
        actualHeaderColumn.setDisplayName("Display Name");
        assertEquals("Column Name", actualHeaderColumn.getColumnName());
        assertEquals("Display Name", actualHeaderColumn.getDisplayName());
    }
}

