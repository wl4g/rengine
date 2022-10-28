///*
// * Copyright 2004-2022 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.client.collector.util;
//
//import java.io.PrintWriter;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.sql.*;
//import java.util.*;
//import java.util.logging.Logger;
//
//import javax.sql.DataSource;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * This is a simple, synchronous, thread-safe database connection pool.
// * <p>
// * REQUIRED PROPERTIES ------------------- JDBC.Driver JDBC.ConnectionURL
// * JDBC.Username JDBC.Password
// * <p>
// * Pool.MaximumActiveConnections Pool.MaximumIdleConnections
// * Pool.MaximumCheckoutTime Pool.TimeToWait Pool.PingQuery Pool.PingEnabled
// * Pool.PingConnectionsOlderThan Pool.PingConnectionsNotUsedFor Pool.QuietMode
// * 
// * @see https://github.com/mybatis/ibatis-2/blob/master/src/main/java/com/ibatis/common/jdbc/SimpleDataSource.java
// */
//@Slf4j
//public class SimpleDataSource implements DataSource {
//
//    /** The Constant PROP_JDBC_DRIVER. */
//    // Required Properties
//    private static final String PROP_JDBC_DRIVER = "JDBC.Driver";
//
//    /** The Constant PROP_JDBC_URL. */
//    private static final String PROP_JDBC_URL = "JDBC.ConnectionURL";
//
//    /** The Constant PROP_JDBC_USERNAME. */
//    private static final String PROP_JDBC_USERNAME = "JDBC.Username";
//
//    /** The Constant PROP_JDBC_PASSWORD. */
//    private static final String PROP_JDBC_PASSWORD = "JDBC.Password";
//
//    /** The Constant PROP_JDBC_DEFAULT_AUTOCOMMIT. */
//    private static final String PROP_JDBC_DEFAULT_AUTOCOMMIT = "JDBC.DefaultAutoCommit";
//
//    /** The Constant PROP_POOL_MAX_ACTIVE_CONN. */
//    // Optional Properties
//    private static final String PROP_POOL_MAX_ACTIVE_CONN = "Pool.MaximumActiveConnections";
//
//    /** The Constant PROP_POOL_MAX_IDLE_CONN. */
//    private static final String PROP_POOL_MAX_IDLE_CONN = "Pool.MaximumIdleConnections";
//
//    /** The Constant PROP_POOL_MAX_CHECKOUT_TIME. */
//    private static final String PROP_POOL_MAX_CHECKOUT_TIME = "Pool.MaximumCheckoutTime";
//
//    /** The Constant PROP_POOL_TIME_TO_WAIT. */
//    private static final String PROP_POOL_TIME_TO_WAIT = "Pool.TimeToWait";
//
//    /** The Constant PROP_POOL_PING_QUERY. */
//    private static final String PROP_POOL_PING_QUERY = "Pool.PingQuery";
//
//    /** The Constant PROP_POOL_PING_CONN_OLDER_THAN. */
//    private static final String PROP_POOL_PING_CONN_OLDER_THAN = "Pool.PingConnectionsOlderThan";
//
//    /** The Constant PROP_POOL_PING_ENABLED. */
//    private static final String PROP_POOL_PING_ENABLED = "Pool.PingEnabled";
//
//    /** The Constant PROP_POOL_PING_CONN_NOT_USED_FOR. */
//    private static final String PROP_POOL_PING_CONN_NOT_USED_FOR = "Pool.PingConnectionsNotUsedFor";
//
//    /** The expected connection type code. */
//    private int expectedConnectionTypeCode;
//
//    /** The Constant ADD_DRIVER_PROPS_PREFIX. */
//    // Additional Driver Properties prefix
//    private static final String ADD_DRIVER_PROPS_PREFIX = "Driver.";
//
//    /** The Constant ADD_DRIVER_PROPS_PREFIX_LENGTH. */
//    private static final int ADD_DRIVER_PROPS_PREFIX_LENGTH = ADD_DRIVER_PROPS_PREFIX.length();
//
//    /** The pool lock. */
//    // ----- BEGIN: FIELDS LOCKED BY POOL_LOCK -----
//    private final Object POOL_LOCK = new Object();
//
//    /** The idle connections. */
//    private List<SimplePooledConnection> idleConnections = new ArrayList<>();
//
//    /** The active connections. */
//    private List<SimplePooledConnection> activeConnections = new ArrayList<>();
//
//    /** The request count. */
//    private long requestCount = 0;
//
//    /** The accumulated request time. */
//    private long accumulatedRequestTime = 0;
//
//    /** The accumulated checkout time. */
//    private long accumulatedCheckoutTime = 0;
//
//    /** The claimed overdue connection count. */
//    private long claimedOverdueConnectionCount = 0;
//
//    /** The accumulated checkout time of overdue connections. */
//    private long accumulatedCheckoutTimeOfOverdueConnections = 0;
//
//    /** The accumulated wait time. */
//    private long accumulatedWaitTime = 0;
//
//    /** The had to wait count. */
//    private long hadToWaitCount = 0;
//
//    /** The bad connection count. */
//    private long badConnectionCount = 0;
//    // ----- END: FIELDS LOCKED BY POOL_LOCK -----
//
//    /** The jdbc driver. */
//    // ----- BEGIN: PROPERTY FIELDS FOR CONFIGURATION -----
//    private String jdbcDriver;
//
//    /** The jdbc url. */
//    private String jdbcUrl;
//
//    /** The jdbc username. */
//    private String jdbcUsername;
//
//    /** The jdbc password. */
//    private String jdbcPassword;
//
//    /** The jdbc default auto commit. */
//    private boolean jdbcDefaultAutoCommit;
//
//    /** The driver props. */
//    private Properties driverProps;
//
//    /** The use driver props. */
//    private boolean useDriverProps;
//
//    /** The pool maximum active connections. */
//    private int poolMaximumActiveConnections;
//
//    /** The pool maximum idle connections. */
//    private int poolMaximumIdleConnections;
//
//    /** The pool maximum checkout time. */
//    private int poolMaximumCheckoutTime;
//
//    /** The pool time to wait. */
//    private int poolTimeToWait;
//
//    /** The pool ping query. */
//    private String poolPingQuery;
//
//    /** The pool ping enabled. */
//    private boolean poolPingEnabled;
//
//    /** The pool ping connections older than. */
//    private int poolPingConnectionsOlderThan;
//
//    /** The pool ping connections not used for. */
//    private int poolPingConnectionsNotUsedFor;
//
//    // ----- END: PROPERTY FIELDS FOR CONFIGURATION -----
//
//    /**
//     * Constructor to allow passing in a map of properties for configuration.
//     *
//     * @param props
//     *            - the configuration parameters
//     */
//    public SimpleDataSource(Map<String, String> props) {
//        initialize(props);
//    }
//
//    /**
//     * Initialize.
//     *
//     * @param props
//     *            the props
//     */
//    private void initialize(Map<String, String> props) {
//        try {
//            String prop_pool_ping_query = null;
//
//            if (props == null) {
//                throw new RuntimeException("SimpleDataSource: The properties map passed to the initializer was null.");
//            }
//
//            if (!(props.containsKey(PROP_JDBC_DRIVER) && props.containsKey(PROP_JDBC_URL) && props.containsKey(PROP_JDBC_USERNAME)
//                    && props.containsKey(PROP_JDBC_PASSWORD))) {
//                throw new RuntimeException("SimpleDataSource: Some properties were not set.");
//            } else {
//
//                jdbcDriver = (String) props.get(PROP_JDBC_DRIVER);
//                jdbcUrl = (String) props.get(PROP_JDBC_URL);
//                jdbcUsername = (String) props.get(PROP_JDBC_USERNAME);
//                jdbcPassword = (String) props.get(PROP_JDBC_PASSWORD);
//
//                poolMaximumActiveConnections = props.containsKey(PROP_POOL_MAX_ACTIVE_CONN)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_MAX_ACTIVE_CONN))
//                        : 10;
//
//                poolMaximumIdleConnections = props.containsKey(PROP_POOL_MAX_IDLE_CONN)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_MAX_IDLE_CONN))
//                        : 5;
//
//                poolMaximumCheckoutTime = props.containsKey(PROP_POOL_MAX_CHECKOUT_TIME)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_MAX_CHECKOUT_TIME))
//                        : 20000;
//
//                poolTimeToWait = props.containsKey(PROP_POOL_TIME_TO_WAIT)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_TIME_TO_WAIT))
//                        : 20000;
//
//                poolPingEnabled = props.containsKey(PROP_POOL_PING_ENABLED)
//                        && Boolean.valueOf((String) props.get(PROP_POOL_PING_ENABLED)).booleanValue();
//
//                prop_pool_ping_query = (String) props.get(PROP_POOL_PING_QUERY);
//                poolPingQuery = props.containsKey(PROP_POOL_PING_QUERY) ? prop_pool_ping_query : "NO PING QUERY SET";
//
//                poolPingConnectionsOlderThan = props.containsKey(PROP_POOL_PING_CONN_OLDER_THAN)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_PING_CONN_OLDER_THAN))
//                        : 0;
//
//                poolPingConnectionsNotUsedFor = props.containsKey(PROP_POOL_PING_CONN_NOT_USED_FOR)
//                        ? Integer.parseInt((String) props.get(PROP_POOL_PING_CONN_NOT_USED_FOR))
//                        : 0;
//
//                jdbcDefaultAutoCommit = props.containsKey(PROP_JDBC_DEFAULT_AUTOCOMMIT)
//                        && Boolean.valueOf((String) props.get(PROP_JDBC_DEFAULT_AUTOCOMMIT)).booleanValue();
//
//                useDriverProps = false;
//                Iterator<String> propIter = props.keySet().iterator();
//                driverProps = new Properties();
//                driverProps.put("user", jdbcUsername);
//                driverProps.put("password", jdbcPassword);
//                while (propIter.hasNext()) {
//                    String name = (String) propIter.next();
//                    String value = (String) props.get(name);
//                    if (name.startsWith(ADD_DRIVER_PROPS_PREFIX)) {
//                        driverProps.put(name.substring(ADD_DRIVER_PROPS_PREFIX_LENGTH), value);
//                        useDriverProps = true;
//                    }
//                }
//
//                expectedConnectionTypeCode = assembleConnectionTypeCode(jdbcUrl, jdbcUsername, jdbcPassword);
//
//                Resources.instantiate(jdbcDriver);
//
//                if (poolPingEnabled && (!props.containsKey(PROP_POOL_PING_QUERY) || prop_pool_ping_query.trim().length() == 0)) {
//                    throw new RuntimeException("SimpleDataSource: property '" + PROP_POOL_PING_ENABLED
//                            + "' is true, but property '" + PROP_POOL_PING_QUERY + "' is not set correctly.");
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("SimpleDataSource: Error while loading properties. Cause: " + e.toString(), e);
//            throw new RuntimeException("SimpleDataSource: Error while loading properties. Cause: " + e, e);
//        }
//    }
//
//    /**
//     * Assemble connection type code.
//     *
//     * @param url
//     *            the url
//     * @param username
//     *            the username
//     * @param password
//     *            the password
//     *
//     * @return the int
//     */
//    private int assembleConnectionTypeCode(String url, String username, String password) {
//        return ("" + url + username + password).hashCode();
//    }
//
//    /**
//     * @see javax.sql.DataSource#getConnection()
//     */
//    public Connection getConnection() throws SQLException {
//        return popConnection(jdbcUsername, jdbcPassword).getProxyConnection();
//    }
//
//    /**
//     * @see javax.sql.DataSource#getConnection(java.lang.String,
//     *      java.lang.String)
//     */
//    public Connection getConnection(String username, String password) throws SQLException {
//        return popConnection(username, password).getProxyConnection();
//    }
//
//    /**
//     * @see javax.sql.DataSource#setLoginTimeout(int)
//     */
//    public void setLoginTimeout(int loginTimeout) throws SQLException {
//        DriverManager.setLoginTimeout(loginTimeout);
//    }
//
//    /**
//     * @see javax.sql.DataSource#getLoginTimeout()
//     */
//    public int getLoginTimeout() throws SQLException {
//        return DriverManager.getLoginTimeout();
//    }
//
//    /**
//     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
//     */
//    public void setLogWriter(PrintWriter logWriter) throws SQLException {
//        DriverManager.setLogWriter(logWriter);
//    }
//
//    /**
//     * @see javax.sql.DataSource#getLogWriter()
//     */
//    public PrintWriter getLogWriter() throws SQLException {
//        return DriverManager.getLogWriter();
//    }
//
//    /**
//     * If a connection has not been used in this many milliseconds, ping the
//     * database to make sure the connection is still good.
//     *
//     * @return the number of milliseconds of inactivity that will trigger a ping
//     */
//    public int getPoolPingConnectionsNotUsedFor() {
//        return poolPingConnectionsNotUsedFor;
//    }
//
//    /**
//     * Getter for the name of the JDBC driver class used.
//     *
//     * @return The name of the class
//     */
//    public String getJdbcDriver() {
//        return jdbcDriver;
//    }
//
//    /**
//     * Getter of the JDBC URL used.
//     *
//     * @return The JDBC URL
//     */
//    public String getJdbcUrl() {
//        return jdbcUrl;
//    }
//
//    /**
//     * Getter for the JDBC user name used.
//     *
//     * @return The user name
//     */
//    public String getJdbcUsername() {
//        return jdbcUsername;
//    }
//
//    /**
//     * Getter for the JDBC password used.
//     *
//     * @return The password
//     */
//    public String getJdbcPassword() {
//        return jdbcPassword;
//    }
//
//    /**
//     * Getter for the maximum number of active connections.
//     *
//     * @return The maximum number of active connections
//     */
//    public int getPoolMaximumActiveConnections() {
//        return poolMaximumActiveConnections;
//    }
//
//    /**
//     * Getter for the maximum number of idle connections.
//     *
//     * @return The maximum number of idle connections
//     */
//    public int getPoolMaximumIdleConnections() {
//        return poolMaximumIdleConnections;
//    }
//
//    /**
//     * Getter for the maximum time a connection can be used before it *may* be
//     * given away again.
//     *
//     * @return The maximum time
//     */
//    public int getPoolMaximumCheckoutTime() {
//        return poolMaximumCheckoutTime;
//    }
//
//    /**
//     * Getter for the time to wait before retrying to get a connection.
//     *
//     * @return The time to wait
//     */
//    public int getPoolTimeToWait() {
//        return poolTimeToWait;
//    }
//
//    /**
//     * Getter for the query to be used to check a connection.
//     *
//     * @return The query
//     */
//    public String getPoolPingQuery() {
//        return poolPingQuery;
//    }
//
//    /**
//     * Getter to tell if we should use the ping query.
//     *
//     * @return True if we need to check a connection before using it
//     */
//    public boolean isPoolPingEnabled() {
//        return poolPingEnabled;
//    }
//
//    /**
//     * Getter for the age of connections that should be pinged before using.
//     *
//     * @return The age
//     */
//    public int getPoolPingConnectionsOlderThan() {
//        return poolPingConnectionsOlderThan;
//    }
//
//    /**
//     * Gets the expected connection type code.
//     *
//     * @return the expected connection type code
//     */
//    private int getExpectedConnectionTypeCode() {
//        return expectedConnectionTypeCode;
//    }
//
//    /**
//     * Getter for the number of connection requests made.
//     *
//     * @return The number of connection requests made
//     */
//    public long getRequestCount() {
//        synchronized (POOL_LOCK) {
//            return requestCount;
//        }
//    }
//
//    /**
//     * Getter for the average time required to get a connection to the database.
//     *
//     * @return The average time
//     */
//    public long getAverageRequestTime() {
//        synchronized (POOL_LOCK) {
//            return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
//        }
//    }
//
//    /**
//     * Getter for the average time spent waiting for connections that were in
//     * use.
//     *
//     * @return The average time
//     */
//    public long getAverageWaitTime() {
//        synchronized (POOL_LOCK) {
//            return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
//        }
//    }
//
//    /**
//     * Getter for the number of requests that had to wait for connections that
//     * were in use.
//     *
//     * @return The number of requests that had to wait
//     */
//    public long getHadToWaitCount() {
//        synchronized (POOL_LOCK) {
//            return hadToWaitCount;
//        }
//    }
//
//    /**
//     * Getter for the number of invalid connections that were found in the pool.
//     *
//     * @return The number of invalid connections
//     */
//    public long getBadConnectionCount() {
//        synchronized (POOL_LOCK) {
//            return badConnectionCount;
//        }
//    }
//
//    /**
//     * Getter for the number of connections that were claimed before they were
//     * returned.
//     *
//     * @return The number of connections
//     */
//    public long getClaimedOverdueConnectionCount() {
//        synchronized (POOL_LOCK) {
//            return claimedOverdueConnectionCount;
//        }
//    }
//
//    /**
//     * Getter for the average age of overdue connections.
//     *
//     * @return The average age
//     */
//    public long getAverageOverdueCheckoutTime() {
//        synchronized (POOL_LOCK) {
//            return claimedOverdueConnectionCount == 0 ? 0
//                    : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
//        }
//    }
//
//    /**
//     * Getter for the average age of a connection checkout.
//     *
//     * @return The average age
//     */
//    public long getAverageCheckoutTime() {
//        synchronized (POOL_LOCK) {
//            return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
//        }
//    }
//
//    /**
//     * Returns the status of the connection pool.
//     *
//     * @return The status
//     */
//    public String getStatus() {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("\n===============================================================");
//        builder.append("\n jdbcDriver                     ").append(jdbcDriver);
//        builder.append("\n jdbcUrl                        ").append(jdbcUrl);
//        builder.append("\n jdbcUsername                   ").append(jdbcUsername);
//        builder.append("\n jdbcPassword                   ").append((jdbcPassword == null ? "NULL" : "************"));
//        builder.append("\n poolMaxActiveConnections       ").append(poolMaximumActiveConnections);
//        builder.append("\n poolMaxIdleConnections         ").append(poolMaximumIdleConnections);
//        builder.append("\n poolMaxCheckoutTime            " + poolMaximumCheckoutTime);
//        builder.append("\n poolTimeToWait                 " + poolTimeToWait);
//        builder.append("\n poolPingEnabled                " + poolPingEnabled);
//        builder.append("\n poolPingQuery                  " + poolPingQuery);
//        builder.append("\n poolPingConnectionsOlderThan   " + poolPingConnectionsOlderThan);
//        builder.append("\n poolPingConnectionsNotUsedFor  " + poolPingConnectionsNotUsedFor);
//        builder.append("\n --------------------------------------------------------------");
//        builder.append("\n activeConnections              " + activeConnections.size());
//        builder.append("\n idleConnections                " + idleConnections.size());
//        builder.append("\n requestCount                   " + getRequestCount());
//        builder.append("\n averageRequestTime             " + getAverageRequestTime());
//        builder.append("\n averageCheckoutTime            " + getAverageCheckoutTime());
//        builder.append("\n claimedOverdue                 " + getClaimedOverdueConnectionCount());
//        builder.append("\n averageOverdueCheckoutTime     " + getAverageOverdueCheckoutTime());
//        builder.append("\n hadToWait                      " + getHadToWaitCount());
//        builder.append("\n averageWaitTime                " + getAverageWaitTime());
//        builder.append("\n badConnectionCount             " + getBadConnectionCount());
//        builder.append("\n===============================================================");
//        return builder.toString();
//    }
//
//    /**
//     * Closes all of the connections in the pool.
//     */
//    public void forceCloseAll() {
//        synchronized (POOL_LOCK) {
//            for (int i = activeConnections.size(); i > 0; i--) {
//                try {
//                    SimplePooledConnection conn = (SimplePooledConnection) activeConnections.remove(i - 1);
//                    conn.invalidate();
//
//                    Connection realConn = conn.getRealConnection();
//                    if (!realConn.getAutoCommit()) {
//                        realConn.rollback();
//                    }
//                    realConn.close();
//                } catch (Exception e) {
//                    // ignore
//                }
//            }
//            for (int i = idleConnections.size(); i > 0; i--) {
//                try {
//                    SimplePooledConnection conn = (SimplePooledConnection) idleConnections.remove(i - 1);
//                    conn.invalidate();
//
//                    Connection realConn = conn.getRealConnection();
//                    if (!realConn.getAutoCommit()) {
//                        realConn.rollback();
//                    }
//                    realConn.close();
//                } catch (Exception e) {
//                    // ignore
//                }
//            }
//        }
//        if (log.isDebugEnabled()) {
//            log.debug("SimpleDataSource forcefully closed/removed all connections.");
//        }
//    }
//
//    /**
//     * Push connection.
//     *
//     * @param conn
//     *            the conn
//     *
//     * @throws SQLException
//     *             the SQL exception
//     */
//    private void pushConnection(SimplePooledConnection conn) throws SQLException {
//
//        synchronized (POOL_LOCK) {
//            activeConnections.remove(conn);
//            if (conn.isValid()) {
//                if (idleConnections.size() < poolMaximumIdleConnections
//                        && conn.getConnectionTypeCode() == getExpectedConnectionTypeCode()) {
//                    accumulatedCheckoutTime += conn.getCheckoutTime();
//                    if (!conn.getRealConnection().getAutoCommit()) {
//                        conn.getRealConnection().rollback();
//                    }
//                    SimplePooledConnection newConn = new SimplePooledConnection(conn.getRealConnection(), this);
//                    idleConnections.add(newConn);
//                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
//                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
//                    conn.invalidate();
//                    if (log.isDebugEnabled()) {
//                        log.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
//                    }
//                    POOL_LOCK.notifyAll();
//                } else {
//                    accumulatedCheckoutTime += conn.getCheckoutTime();
//                    if (!conn.getRealConnection().getAutoCommit()) {
//                        conn.getRealConnection().rollback();
//                    }
//                    conn.getRealConnection().close();
//                    if (log.isDebugEnabled()) {
//                        log.debug("Closed connection " + conn.getRealHashCode() + ".");
//                    }
//                    conn.invalidate();
//                }
//            } else {
//                if (log.isDebugEnabled()) {
//                    log.debug("A bad connection (" + conn.getRealHashCode()
//                            + ") attempted to return to the pool, discarding connection.");
//                }
//                badConnectionCount++;
//            }
//        }
//    }
//
//    /**
//     * Pop connection.
//     *
//     * @param username
//     *            the username
//     * @param password
//     *            the password
//     *
//     * @return the simple pooled connection
//     *
//     * @throws SQLException
//     *             the SQL exception
//     */
//    private SimplePooledConnection popConnection(String username, String password) throws SQLException {
//        boolean countedWait = false;
//        SimplePooledConnection conn = null;
//        long t = System.currentTimeMillis();
//        int localBadConnectionCount = 0;
//
//        while (conn == null) {
//            synchronized (POOL_LOCK) {
//                if (idleConnections.size() > 0) {
//                    // Pool has available connection
//                    conn = (SimplePooledConnection) idleConnections.remove(0);
//                    if (log.isDebugEnabled()) {
//                        log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
//                    }
//                } else {
//                    // Pool does not have available connection
//                    if (activeConnections.size() < poolMaximumActiveConnections) {
//                        // Can create new connection
//                        if (useDriverProps) {
//                            conn = new SimplePooledConnection(DriverManager.getConnection(jdbcUrl, driverProps), this);
//                        } else {
//                            conn = new SimplePooledConnection(DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword),
//                                    this);
//                        }
//                        Connection realConn = conn.getRealConnection();
//                        if (realConn.getAutoCommit() != jdbcDefaultAutoCommit) {
//                            realConn.setAutoCommit(jdbcDefaultAutoCommit);
//                        }
//                        if (log.isDebugEnabled()) {
//                            log.debug("Created connection " + conn.getRealHashCode() + ".");
//                        }
//                    } else {
//                        // Cannot create new connection
//                        SimplePooledConnection oldestActiveConnection = (SimplePooledConnection) activeConnections.get(0);
//                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
//                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
//                            // Can claim overdue connection
//                            claimedOverdueConnectionCount++;
//                            accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
//                            accumulatedCheckoutTime += longestCheckoutTime;
//                            activeConnections.remove(oldestActiveConnection);
//                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
//                                oldestActiveConnection.getRealConnection().rollback();
//                            }
//                            conn = new SimplePooledConnection(oldestActiveConnection.getRealConnection(), this);
//                            oldestActiveConnection.invalidate();
//                            if (log.isDebugEnabled()) {
//                                log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
//                            }
//                        } else {
//                            // Must wait
//                            try {
//                                if (!countedWait) {
//                                    hadToWaitCount++;
//                                    countedWait = true;
//                                }
//                                if (log.isDebugEnabled()) {
//                                    log.debug("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
//                                }
//                                long wt = System.currentTimeMillis();
//                                POOL_LOCK.wait(poolTimeToWait);
//                                accumulatedWaitTime += System.currentTimeMillis() - wt;
//                            } catch (InterruptedException e) {
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (conn != null) {
//                    if (conn.isValid()) {
//                        if (!conn.getRealConnection().getAutoCommit()) {
//                            conn.getRealConnection().rollback();
//                        }
//                        conn.setConnectionTypeCode(assembleConnectionTypeCode(jdbcUrl, username, password));
//                        conn.setCheckoutTimestamp(System.currentTimeMillis());
//                        conn.setLastUsedTimestamp(System.currentTimeMillis());
//                        activeConnections.add(conn);
//                        requestCount++;
//                        accumulatedRequestTime += System.currentTimeMillis() - t;
//                    } else {
//                        if (log.isDebugEnabled()) {
//                            log.debug("A bad connection (" + conn.getRealHashCode()
//                                    + ") was returned from the pool, getting another connection.");
//                        }
//                        badConnectionCount++;
//                        localBadConnectionCount++;
//                        conn = null;
//                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
//                            if (log.isDebugEnabled()) {
//                                log.debug("SimpleDataSource: Could not get a good connection to the database.");
//                            }
//                            throw new SQLException("SimpleDataSource: Could not get a good connection to the database.");
//                        }
//                    }
//                }
//            }
//
//        }
//
//        if (conn == null) {
//            if (log.isDebugEnabled()) {
//                log.debug("SimpleDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
//            }
//            throw new SQLException(
//                    "SimpleDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
//        }
//
//        return conn;
//    }
//
//    /**
//     * Method to check to see if a connection is still usable.
//     *
//     * @param conn
//     *            - the connection to check
//     *
//     * @return True if the connection is still usable
//     */
//    private boolean pingConnection(SimplePooledConnection conn) {
//        boolean result = true;
//
//        try {
//            result = !conn.getRealConnection().isClosed();
//        } catch (SQLException e) {
//            if (log.isDebugEnabled()) {
//                log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
//            }
//            result = false;
//        }
//
//        if (result) {
//            if (poolPingEnabled) {
//                if ((poolPingConnectionsOlderThan > 0 && conn.getAge() > poolPingConnectionsOlderThan)
//                        || (poolPingConnectionsNotUsedFor > 0
//                                && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor)) {
//
//                    try {
//                        if (log.isDebugEnabled()) {
//                            log.debug("Testing connection " + conn.getRealHashCode() + " ...");
//                        }
//                        Connection realConn = conn.getRealConnection();
//                        Statement statement = realConn.createStatement();
//                        ResultSet rs = statement.executeQuery(poolPingQuery);
//                        rs.close();
//                        statement.close();
//                        if (!realConn.getAutoCommit()) {
//                            realConn.rollback();
//                        }
//                        result = true;
//                        if (log.isDebugEnabled()) {
//                            log.debug("Connection " + conn.getRealHashCode() + " is GOOD!");
//                        }
//                    } catch (Exception e) {
//                        log.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
//                        try {
//                            conn.getRealConnection().close();
//                        } catch (Exception e2) {
//                            // ignore
//                        }
//                        result = false;
//                        if (log.isDebugEnabled()) {
//                            log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
//                        }
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Unwraps a pooled connection to get to the 'real' connection.
//     *
//     * @param conn
//     *            - the pooled connection to unwrap
//     *
//     * @return The 'real' connection
//     */
//    public static Connection unwrapConnection(Connection conn) {
//        if (conn instanceof SimplePooledConnection) {
//            return ((SimplePooledConnection) conn).getRealConnection();
//        } else {
//            return conn;
//        }
//    }
//
//    protected void finalize() throws Throwable {
//        forceCloseAll();
//    }
//
//    /**
//     * ---------------------------------------------------------------------------------------
//     * SimplePooledConnection
//     * ---------------------------------------------------------------------------------------.
//     */
//    public static class SimplePooledConnection implements InvocationHandler {
//
//        /** The Constant CLOSE. */
//        private static final String CLOSE = "close";
//
//        /** The Constant IFACES. */
//        private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };
//
//        /** The hash code. */
//        private int hashCode = 0;
//
//        /** The data source. */
//        private SimpleDataSource dataSource;
//
//        /** The real connection. */
//        private Connection realConnection;
//
//        /** The proxy connection. */
//        private Connection proxyConnection;
//
//        /** The checkout timestamp. */
//        private long checkoutTimestamp;
//
//        /** The created timestamp. */
//        private long createdTimestamp;
//
//        /** The last used timestamp. */
//        private long lastUsedTimestamp;
//
//        /** The connection type code. */
//        private int connectionTypeCode;
//
//        /** The valid. */
//        private boolean valid;
//
//        /**
//         * Constructor for SimplePooledConnection that uses the Connection and
//         * SimpleDataSource passed in.
//         *
//         * @param connection
//         *            - the connection that is to be presented as a pooled
//         *            connection
//         * @param dataSource
//         *            - the dataSource that the connection is from
//         */
//        public SimplePooledConnection(Connection connection, SimpleDataSource dataSource) {
//            this.hashCode = connection.hashCode();
//            this.realConnection = connection;
//            this.dataSource = dataSource;
//            this.createdTimestamp = System.currentTimeMillis();
//            this.lastUsedTimestamp = System.currentTimeMillis();
//            this.valid = true;
//
//            proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
//        }
//
//        /**
//         * Invalidates the connection.
//         */
//        public void invalidate() {
//            valid = false;
//        }
//
//        /**
//         * Method to see if the connection is usable.
//         *
//         * @return True if the connection is usable
//         */
//        public boolean isValid() {
//            return valid && realConnection != null && dataSource.pingConnection(this);
//        }
//
//        /**
//         * Getter for the *real* connection that this wraps.
//         *
//         * @return The connection
//         */
//        public Connection getRealConnection() {
//            return realConnection;
//        }
//
//        /**
//         * Getter for the proxy for the connection.
//         *
//         * @return The proxy
//         */
//        public Connection getProxyConnection() {
//            return proxyConnection;
//        }
//
//        /**
//         * Gets the hashcode of the real connection (or 0 if it is null).
//         *
//         * @return The hashcode of the real connection (or 0 if it is null)
//         */
//        public int getRealHashCode() {
//            if (realConnection == null) {
//                return 0;
//            } else {
//                return realConnection.hashCode();
//            }
//        }
//
//        /**
//         * Getter for the connection type (based on url + user + password).
//         *
//         * @return The connection type
//         */
//        public int getConnectionTypeCode() {
//            return connectionTypeCode;
//        }
//
//        /**
//         * Setter for the connection type.
//         *
//         * @param connectionTypeCode
//         *            - the connection type
//         */
//        public void setConnectionTypeCode(int connectionTypeCode) {
//            this.connectionTypeCode = connectionTypeCode;
//        }
//
//        /**
//         * Getter for the time that the connection was created.
//         *
//         * @return The creation timestamp
//         */
//        public long getCreatedTimestamp() {
//            return createdTimestamp;
//        }
//
//        /**
//         * Setter for the time that the connection was created.
//         *
//         * @param createdTimestamp
//         *            - the timestamp
//         */
//        public void setCreatedTimestamp(long createdTimestamp) {
//            this.createdTimestamp = createdTimestamp;
//        }
//
//        /**
//         * Getter for the time that the connection was last used.
//         *
//         * @return - the timestamp
//         */
//        public long getLastUsedTimestamp() {
//            return lastUsedTimestamp;
//        }
//
//        /**
//         * Setter for the time that the connection was last used.
//         *
//         * @param lastUsedTimestamp
//         *            - the timestamp
//         */
//        public void setLastUsedTimestamp(long lastUsedTimestamp) {
//            this.lastUsedTimestamp = lastUsedTimestamp;
//        }
//
//        /**
//         * Getter for the time since this connection was last used.
//         *
//         * @return - the time since the last use
//         */
//        public long getTimeElapsedSinceLastUse() {
//            return System.currentTimeMillis() - lastUsedTimestamp;
//        }
//
//        /**
//         * Getter for the age of the connection.
//         *
//         * @return the age
//         */
//        public long getAge() {
//            return System.currentTimeMillis() - createdTimestamp;
//        }
//
//        /**
//         * Getter for the timestamp that this connection was checked out.
//         *
//         * @return the timestamp
//         */
//        public long getCheckoutTimestamp() {
//            return checkoutTimestamp;
//        }
//
//        /**
//         * Setter for the timestamp that this connection was checked out.
//         *
//         * @param timestamp
//         *            the timestamp
//         */
//        public void setCheckoutTimestamp(long timestamp) {
//            this.checkoutTimestamp = timestamp;
//        }
//
//        /**
//         * Getter for the time that this connection has been checked out.
//         *
//         * @return the time
//         */
//        public long getCheckoutTime() {
//            return System.currentTimeMillis() - checkoutTimestamp;
//        }
//
//        /**
//         * Gets the valid connection.
//         *
//         * @return the valid connection
//         */
//        private Connection getValidConnection() {
//            if (!valid) {
//                throw new RuntimeException("Error accessing SimplePooledConnection. Connection is invalid.");
//            }
//            return realConnection;
//        }
//
//        public int hashCode() {
//            return hashCode;
//        }
//
//        /**
//         * Allows comparing this connection to another
//         *
//         * @param obj
//         *            - the other connection to test for equality
//         *
//         * @see java.lang.Object#equals(java.lang.Object)
//         */
//        public boolean equals(Object obj) {
//            if (obj instanceof SimplePooledConnection) {
//                return realConnection.hashCode() == (((SimplePooledConnection) obj).realConnection.hashCode());
//            } else if (obj instanceof Connection) {
//                return hashCode == obj.hashCode();
//            } else {
//                return false;
//            }
//        }
//
//        // **********************************
//        // Implemented Connection Methods -- Now handled by proxy
//        // **********************************
//
//        /**
//         * Required for InvocationHandler implementation.
//         *
//         * @param proxy
//         *            - not used
//         * @param method
//         *            - the method to be executed
//         * @param args
//         *            - the parameters to be passed to the method
//         *
//         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
//         *      java.lang.reflect.Method, java.lang.Object[])
//         */
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            String methodName = method.getName();
//            if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
//                dataSource.pushConnection(this);
//                return null;
//            } else {
//                try {
//                    return method.invoke(getValidConnection(), args);
//                } catch (Throwable t) {
//                    throw ClassInfo.unwrapThrowable(t);
//                }
//            }
//        }
//
//        /**
//         * Creates the statement.
//         *
//         * @return the statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Statement createStatement() throws SQLException {
//            return getValidConnection().createStatement();
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(String sql) throws SQLException {
//            return getValidConnection().prepareStatement(sql);
//        }
//
//        /**
//         * Prepare call.
//         *
//         * @param sql
//         *            the sql
//         *
//         * @return the callable statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public CallableStatement prepareCall(String sql) throws SQLException {
//            return getValidConnection().prepareCall(sql);
//        }
//
//        /**
//         * Native SQL.
//         *
//         * @param sql
//         *            the sql
//         *
//         * @return the string
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public String nativeSQL(String sql) throws SQLException {
//            return getValidConnection().nativeSQL(sql);
//        }
//
//        /**
//         * Sets the auto commit.
//         *
//         * @param autoCommit
//         *            the new auto commit
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setAutoCommit(boolean autoCommit) throws SQLException {
//            getValidConnection().setAutoCommit(autoCommit);
//        }
//
//        /**
//         * Gets the auto commit.
//         *
//         * @return the auto commit
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public boolean getAutoCommit() throws SQLException {
//            return getValidConnection().getAutoCommit();
//        }
//
//        /**
//         * Commit.
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void commit() throws SQLException {
//            getValidConnection().commit();
//        }
//
//        /**
//         * Rollback.
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void rollback() throws SQLException {
//            getValidConnection().rollback();
//        }
//
//        /**
//         * Close.
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void close() throws SQLException {
//            dataSource.pushConnection(this);
//        }
//
//        /**
//         * Checks if is closed.
//         *
//         * @return true, if is closed
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public boolean isClosed() throws SQLException {
//            return getValidConnection().isClosed();
//        }
//
//        /**
//         * Gets the meta data.
//         *
//         * @return the meta data
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public DatabaseMetaData getMetaData() throws SQLException {
//            return getValidConnection().getMetaData();
//        }
//
//        /**
//         * Sets the read only.
//         *
//         * @param readOnly
//         *            the new read only
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setReadOnly(boolean readOnly) throws SQLException {
//            getValidConnection().setReadOnly(readOnly);
//        }
//
//        /**
//         * Checks if is read only.
//         *
//         * @return true, if is read only
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public boolean isReadOnly() throws SQLException {
//            return getValidConnection().isReadOnly();
//        }
//
//        /**
//         * Sets the catalog.
//         *
//         * @param catalog
//         *            the new catalog
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setCatalog(String catalog) throws SQLException {
//            getValidConnection().setCatalog(catalog);
//        }
//
//        /**
//         * Gets the catalog.
//         *
//         * @return the catalog
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public String getCatalog() throws SQLException {
//            return getValidConnection().getCatalog();
//        }
//
//        /**
//         * Sets the transaction isolation.
//         *
//         * @param level
//         *            the new transaction isolation
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setTransactionIsolation(int level) throws SQLException {
//            getValidConnection().setTransactionIsolation(level);
//        }
//
//        /**
//         * Gets the transaction isolation.
//         *
//         * @return the transaction isolation
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public int getTransactionIsolation() throws SQLException {
//            return getValidConnection().getTransactionIsolation();
//        }
//
//        /**
//         * Gets the warnings.
//         *
//         * @return the warnings
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public SQLWarning getWarnings() throws SQLException {
//            return getValidConnection().getWarnings();
//        }
//
//        /**
//         * Clear warnings.
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void clearWarnings() throws SQLException {
//            getValidConnection().clearWarnings();
//        }
//
//        /**
//         * Creates the statement.
//         *
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         *
//         * @return the statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
//            return getValidConnection().createStatement(resultSetType, resultSetConcurrency);
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
//            return getValidConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
//        }
//
//        /**
//         * Prepare call.
//         *
//         * @param sql
//         *            the sql
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         *
//         * @return the callable statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
//            return getValidConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
//        }
//
//        /**
//         * Gets the type map.
//         *
//         * @return the type map
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Map<String, Class<?>> getTypeMap() throws SQLException {
//            return getValidConnection().getTypeMap();
//        }
//
//        /**
//         * Sets the type map.
//         *
//         * @param map
//         *            the new type map
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
//            getValidConnection().setTypeMap(map);
//        }
//
//        // **********************************
//        // JDK 1.4 JDBC 3.0 Methods below
//        // **********************************
//
//        /**
//         * Sets the holdability.
//         *
//         * @param holdability
//         *            the new holdability
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void setHoldability(int holdability) throws SQLException {
//            getValidConnection().setHoldability(holdability);
//        }
//
//        /**
//         * Gets the holdability.
//         *
//         * @return the holdability
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public int getHoldability() throws SQLException {
//            return getValidConnection().getHoldability();
//        }
//
//        /**
//         * Sets the savepoint.
//         *
//         * @return the savepoint
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Savepoint setSavepoint() throws SQLException {
//            return getValidConnection().setSavepoint();
//        }
//
//        /**
//         * Sets the savepoint.
//         *
//         * @param name
//         *            the name
//         *
//         * @return the savepoint
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Savepoint setSavepoint(String name) throws SQLException {
//            return getValidConnection().setSavepoint(name);
//        }
//
//        /**
//         * Rollback.
//         *
//         * @param savepoint
//         *            the savepoint
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void rollback(Savepoint savepoint) throws SQLException {
//            getValidConnection().rollback(savepoint);
//        }
//
//        /**
//         * Release savepoint.
//         *
//         * @param savepoint
//         *            the savepoint
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
//            getValidConnection().releaseSavepoint(savepoint);
//        }
//
//        /**
//         * Creates the statement.
//         *
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         * @param resultSetHoldability
//         *            the result set holdability
//         *
//         * @return the statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
//                throws SQLException {
//            return getValidConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         * @param resultSetHoldability
//         *            the result set holdability
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(
//                String sql,
//                int resultSetType,
//                int resultSetConcurrency,
//                int resultSetHoldability) throws SQLException {
//            return getValidConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//        }
//
//        /**
//         * Prepare call.
//         *
//         * @param sql
//         *            the sql
//         * @param resultSetType
//         *            the result set type
//         * @param resultSetConcurrency
//         *            the result set concurrency
//         * @param resultSetHoldability
//         *            the result set holdability
//         *
//         * @return the callable statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
//                throws SQLException {
//            return getValidConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         * @param autoGeneratedKeys
//         *            the auto generated keys
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
//            return getValidConnection().prepareStatement(sql, autoGeneratedKeys);
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         * @param columnIndexes
//         *            the column indexes
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
//            return getValidConnection().prepareStatement(sql, columnIndexes);
//        }
//
//        /**
//         * Prepare statement.
//         *
//         * @param sql
//         *            the sql
//         * @param columnNames
//         *            the column names
//         *
//         * @return the prepared statement
//         *
//         * @throws SQLException
//         *             the SQL exception
//         */
//        public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
//            return getValidConnection().prepareStatement(sql, columnNames);
//        }
//
//    }
//
//    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public <T> T unwrap(Class<T> iface) throws SQLException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public boolean isWrapperFor(Class<?> iface) throws SQLException {
//        // TODO Auto-generated method stub
//        return false;
//    }
//}