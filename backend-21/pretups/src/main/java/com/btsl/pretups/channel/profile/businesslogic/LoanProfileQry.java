package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;

public interface LoanProfileQry {
	public PreparedStatement addLoanProfileQry(Connection con, LoanProfileCombinedVO loanProfileVO) throws  SQLException, BTSLBaseException;
	
	public PreparedStatement addLoanProfileDetailsQry(Connection con, LoanProfileDetailsVO slabVO, String profileID) throws  SQLException, BTSLBaseException;

	public PreparedStatement isProfileNameExistsQry(Connection con, String profileName) throws  SQLException;

	public PreparedStatement loadLoanProfilesQry(Connection con, String categoryCode, String networkCode) throws  SQLException;

	public PreparedStatement loadLoanProfileSlabsQry(Connection con, String profileID) throws SQLException;

	public PreparedStatement loadLoanProfileByIdQry(Connection con, String profileID) throws SQLException;

	public PreparedStatement updateLoanProfileQry(Connection con, LoanProfileCombinedVO loanProfileVO) throws SQLException;

	public PreparedStatement deleteLoanSlabsById(Connection con, String profileID) throws SQLException;

	public PreparedStatement deleteLoanProfileQry(Connection con, String profileID) throws SQLException;

	public PreparedStatement isProfileAssociatedQry(Connection con, String profileID) throws SQLException;
}
