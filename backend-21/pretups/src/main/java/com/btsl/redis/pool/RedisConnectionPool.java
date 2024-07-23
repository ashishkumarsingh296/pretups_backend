package com.btsl.redis.pool;

import com.btsl.redis.util.HostPort;
import com.btsl.redis.util.RedisActivityLog;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionPool {
	private static Object staticLock = new Object();
    private static JedisPool pool;
    private static String host;
    private static int port; // 6379 for NonSSL, 6380 for SSL
    private static int connectTimeout; //milliseconds
    private static int operationTimeout; //milliseconds
    private static String password;
    private static int maxConnections;
    private static JedisPoolConfig config;

    // Should be called exactly once during App Startup logic.
    public static void initializeSettings() {
    	RedisConnectionPool.host = HostPort.getRedisHost();
    	RedisConnectionPool.port = HostPort.getRedisPort();
    	RedisConnectionPool.password = HostPort.getPassword();
    	RedisConnectionPool.connectTimeout = HostPort.getConnectionTimeout();
    	RedisConnectionPool.operationTimeout = HostPort.getConnectionTimeout();
    	RedisConnectionPool.maxConnections = HostPort.getMaxConnection();
    	RedisConnectionPool.setJedisPoolConfig();
    }
    
    // MAKE SURE to call the initializeSettings method first
    public static JedisPool getPoolInstance() {
        if (pool == null) { // avoid synchronization lock if initialization has already happened
            synchronized(staticLock) {
                if (pool == null) { // don't re-initialize if another thread beat us to it.
                    pool = new JedisPool(config, host, port);//it may change according to parameters 
                      /*new JedisPool(
                    		config,
                    		host,
                    		port,
                    		connectTimeout,
                    		password
                        );*/
                }
              
                RedisActivityLog.log("Active Con :"+pool.getNumActive()+" ,Idle Con :"+pool.getNumIdle()+" ,Waiters Con :"+pool.getNumWaiters());
            }
        }
        RedisActivityLog.log("Active Con :"+pool.getNumActive()+" ,Idle Con :"+pool.getNumIdle()+" ,Waiters Con :"+pool.getNumWaiters());
        return pool;
    }
    
    public static Jedis getJedisPoolResource() {
    	Jedis jedis = pool.getResource();
    	RedisActivityLog.log("Active Con :"+pool.getNumActive()+" ,Idle Conn :"+pool.getNumIdle());
    	return jedis;
    }

    public static void setJedisPoolConfig() {
        if (config == null) {
        	config = new JedisPoolConfig();

            // Each thread trying to access Redis needs its own Jedis instance from the pool.
            config.setMaxTotal(maxConnections);
            config.setMaxIdle(maxConnections);

            // Using "false" here will make it easier to debug when your maxTotal/minIdle/etc settings need adjusting.
            // Setting it to "true" will result better behavior when unexpected load hits in production
            config.setBlockWhenExhausted(true);

            // How long to wait before throwing when pool is exhausted
            config.setMaxWaitMillis(operationTimeout);

            // This controls the number of connections that should be maintained for bursts of load.
            // Increase this value when you see pool.getResource() taking a long time to complete under burst scenarios
            config.setMinIdle(50);

        }

    } 
}
