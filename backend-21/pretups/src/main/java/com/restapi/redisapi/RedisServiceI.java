package com.restapi.redisapi;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

public interface RedisServiceI {
	/**
	 * 
	 * @param con
	 * @param response 
	 * @param responseSwag
	 * @return 
	 */
	public LookupsCacheResponse lookupsCache(Connection con, LookupsCacheResponse response, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param con
	 * @param response 
	 * @param responseSwag
	 * @return 
	 */
	
	public SublookupsCacheResponse sublookupsCache(Connection con, SublookupsCacheResponse response, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param con
	 * @param response 
	 * @param responseSwag
	 * @return 
	 */
	
	public PreferenceCacheResponse preferenceCache(Connection con, PreferenceCacheResponse response, HttpServletResponse responseSwag);
	

}
