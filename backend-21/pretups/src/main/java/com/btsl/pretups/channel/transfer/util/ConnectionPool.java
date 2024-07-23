/*
 * Created on Sep 13, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.channel.transfer.util;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ConnectionPool {
    private Log _log = LogFactory.getLog(ConnectionPool.class.getName());
    // Number of initial connections to make.
    private int _initialConnectionCount = 5;
    // Number of initial connections to make.
    private int _maximumConnectionCount = 10;
    // A list of available connections for use.
    private Vector _availableConnections = new Vector();
    // A list of connections being used currently.
    private Vector _usedConnections = new Vector();
    // The URL string used to connect to the database
    private String _urlString = null;
    // The username used to connect to the database
    private String _userName = null;
    // The password used to connect to the database
    private String _password = null;

    // Constructor
    public ConnectionPool(
                    String urlString, String user, String passwd)
                    throws SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug(this, "ConnectionPool()Entered " + getCounts());
        }
        // Initialize the required parameters
        _urlString = urlString;
        _userName = user;
        _password = passwd;

        for (int cnt = 0; cnt < _initialConnectionCount; cnt++) {
            // Add a new connection to the available list.
            _availableConnections.addElement(getConnection());
        }

        if (_log.isDebugEnabled()) {
            _log.debug(this, "ConnectionPool()Exiting " + getCounts());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(_urlString, _userName, _password);
    }

    public synchronized Connection replaceWithFreshConnection(Connection p_invalidConn) throws SQLException {
        final String METHOD_NAME = "replaceWithFreshConnection";
        _log.debug(this, "replaceWithFreshConnection()	Entered " + getCounts());
        _usedConnections.removeElement(p_invalidConn);
        try {
            p_invalidConn.close();
            p_invalidConn = null;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        Connection newConnxn = null;
        // Im out of connections. Create one more.
        newConnxn = getConnection();
        // Add this connection to the "Used" list.
        _usedConnections.addElement(newConnxn);
        // We dont have to do anything else since this is
        // a new connection.
        if (_log.isDebugEnabled()) {
            _log.debug(this, "replaceWithFreshConnection()	Exiting " + getCounts());
        }
        return newConnxn;
    }

    public synchronized Connection checkout() throws SQLException {
        _log.debug(this, "checkout()Entered " + getCounts());
        Connection newConnxn = null;
        if (_availableConnections.size() >= _maximumConnectionCount) {
            _log.error(this, "checkout() no. of connections reached " + getCounts());
            return null;
        }

        if (_availableConnections.isEmpty()) {
            // Im out of connections. Create one more.
            newConnxn = getConnection();
            // Add this connection to the "Used" list.
            _usedConnections.addElement(newConnxn);
            // We dont have to do anything else since this is
            // a new connection.
        } else {
            // Connections exist !
            // Get a connection object
            newConnxn = (Connection) _availableConnections.lastElement();
            // Remove it from the available list.
            _availableConnections.removeElement(newConnxn);
            // Add it to the used list.
            _usedConnections.addElement(newConnxn);
        }

        // Either way, we should have a connection object now.
        _log.debug(this, "checkout() Exiting " + getCounts());
        return newConnxn;
    }

    public synchronized void checkin(Connection c) throws SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug(this, "checkin() Entered " + getCounts());
        }
        if (c != null) {
            // Remove from used list.
            _usedConnections.removeElement(c);
            /*
             * if(_availableConnections.isEmpty())
             * {
             * // Im out of connections. Create one more.
             * newConnxn = getConnection();
             * // Add this connection to the "Used" list.
             * _usedConnections.addElement(newConnxn);
             * // We dont have to do anything else since this is
             * // a new connection.
             * }
             */
            if (_availableConnections.size() > _initialConnectionCount) {
                c.close();
            } else {
                _availableConnections.addElement(c);// Add to the available list
            }
        }
        _log.debug(this, "checkin() Exiting " + getCounts());
    }

    public int availableCount() {
        return _availableConnections.size();
    }

    /**
     * @return
     */
    public Vector getAvailableConnections() {
        return _availableConnections;
    }

    /**
     * @return
     */
    public int getInitialConnectionCount() {
        return _initialConnectionCount;
    }

    /**
     * @return
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @return
     */
    public String getUrlString() {
        return _urlString;
    }

    /**
     * @return
     */
    public Vector getUsedConnections() {
        return _usedConnections;
    }

    /**
     * @return
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param vector
     */
    public void setAvailableConnections(Vector vector) {
        _availableConnections = vector;
    }

    /**
     * @param i
     */
    public void setInitialConnectionCount(int i) {
        _initialConnectionCount = i;
    }

    /**
     * @param string
     */
    public void setPassword(String string) {
        _password = string;
    }

    /**
     * @param string
     */
    public void setUrlString(String string) {
        _urlString = string;
    }

    /**
     * @param vector
     */
    public void setUsedConnections(Vector vector) {
        _usedConnections = vector;
    }

    /**
     * @param string
     */
    public void setUserName(String string) {
        _userName = string;
    }

    private String getCounts() {
        return "maximum available :" + _availableConnections.size() + "	used :" + _usedConnections.size() + " allowed is:" + _maximumConnectionCount + " initial count:" + _initialConnectionCount;
    }

    /**
     * @return
     */
    public int getMaximumConnectionCount() {
        return _maximumConnectionCount;
    }

    /**
     * @param i
     */
    public void setMaximumConnectionCount(int i) {
        _maximumConnectionCount = i;
    }

}
