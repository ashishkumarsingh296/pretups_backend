/**
 * 
 */
package com.testscripts.prerequisites;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.classes.BaseTest;

/**
 * @author lokesh.kontey
 *
 */
public class UpdateCache extends BaseTest{

	@Test
	public void updateCache(){
		CacheUpdate cacheupdate = new CacheUpdate(driver);
		
		cacheupdate.updateCache();
	}
}
