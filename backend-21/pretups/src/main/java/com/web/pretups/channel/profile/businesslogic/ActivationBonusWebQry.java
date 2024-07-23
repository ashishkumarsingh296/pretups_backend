package com.web.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

public interface ActivationBonusWebQry{
	public String searchSubscriberMappingQry();
	
	public String searchNewRetailerQry();
	public PreparedStatement viewRedemptionenquiryDetailsQry(Connection con,String domainCode,String categoryCode,String userId,String zoneCode,String fromdate,String todate) throws SQLException,ParseException;
	public String retailerSubsMappListQry();
	public String loadProfileMappingListForDeleteQry();
	public String populateListFromTableQry();
	public PreparedStatement searchNewRetailerProfileQry(Connection con,String id) throws SQLException;
	public String activeUsersListWithProfilesQry();
	public PreparedStatement validateUserdetailsQry(Connection con,String zoneCode,String userIdLoggedUser,String domainCode,String categoryCode,String channelUser) throws SQLException;
	public PreparedStatement loadBonusPointDetailsQry(Connection con,String zoneCode,String userId,String domainCode,String categoryCode,String fromdate,String todate)throws SQLException,ParseException;

} 