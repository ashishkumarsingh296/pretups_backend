package com.Features.mapclasses;

import java.util.HashMap;

import com.classes.CONSTANT;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;

/**
 * @author krishan.chawla
 *
 */
public class NetworkStockMap {

	RandomGeneration randStr = new RandomGeneration();
	
	private HashMap<String, String> defaultMap() {
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		HashMap<String, String> paramMap = new HashMap<>();
		paramMap.put("MultiWalletPreference", DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS));
		paramMap.put("WalletType", PretupsI.SALE_WALLET_LOOKUP);
		paramMap.put("ReferenceNo", RandomGeneration.randomAlphabets(10));
		paramMap.put("InitiationAmount", "2000");
		paramMap.put("Remarks", "Automated Network Stock Testing SIT");
		paramMap.put("action", "submit");
		paramMap.put("approval1Action", "submit");
		paramMap.put("approval2Action", "submit");
			
		return paramMap;
	}
	
	public HashMap<String, String> getNetworkStockMap(String Key, String Value) {	
			HashMap<String, String> instanceMap = defaultMap();
			instanceMap.put(Key, Value);
			return instanceMap;
	}
	
	public HashMap<String, String> getNetworkStockMap() {	
		return defaultMap();
	}
	
}
