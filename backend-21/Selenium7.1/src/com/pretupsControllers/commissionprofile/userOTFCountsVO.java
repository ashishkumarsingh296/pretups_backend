package com.pretupsControllers.commissionprofile;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dbrepository.DBHandler;

public class userOTFCountsVO {
	
	private static String _profileOTFDetailID;
	private static int _otfCount;
	private static long _otfValue;
	
	public String getAdnlComOTFDetailID() {
		return _profileOTFDetailID;
	}
	
	public int getOtfCount() {
		return _otfCount;
	}
	
	public long getOtfValue() {
		return _otfValue;
	}
	
	public void loadUserOTFCounts(String userID, String detailID, boolean addnl) throws SQLException {
		
		ResultSet userOTFCounts = DBHandler.AccessHandler.getuserOTFCounts(userID, detailID);
		while (userOTFCounts.next()) {
			_profileOTFDetailID = userOTFCounts.getString("prfle_otf_detail_id");
			_otfCount = userOTFCounts.getInt("otf_count");
			_otfValue = userOTFCounts.getLong("otf_value");
		}
	}
}
