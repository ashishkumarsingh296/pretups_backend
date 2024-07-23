package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.pointenquiry.businesslogic.PointEnquiryQry;

/**
 * 
 * @author sadhan.k
 *PointEnquiryOracleQry
 */
public class PointEnquiryOracleQry implements PointEnquiryQry{

	@Override
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchy(Connection con,String networkCode,String domain,String categoryCode,String geographicalDomainCode) throws SQLException {
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" SELECT DISTINCT U.user_id, U.user_name ");
        strBuilder.append(" FROM users U, user_geographies UG, categories CAT WHERE U.network_code =? ");
        // if Domain=domain_code and categories=ALL/specific category code,else
        // no where clause check for ALL
        if (!(PretupsI.ALL).equals(domain)) {
            // for all categories
            if ((PretupsI.ALL).equals(categoryCode)) {
                strBuilder.append(" AND cat.domain_code=? ");
            } else {
                strBuilder.append(" AND U.category_code =? ");
            }
        }
        strBuilder.append(" AND u.status not IN(?,?) AND u.user_type =? ");
        strBuilder.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id  AND UG.grph_domain_code ");

        strBuilder.append(" IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status =?  ");
        strBuilder.append("  CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code  START WITH grph_domain_code ");
        strBuilder.append("  IN (SELECT grph_domain_code FROM user_geographies UG1 ");
        // When All geography is selected
        if (!(PretupsI.ALL).equals(geographicalDomainCode)) {
            strBuilder.append(" WHERE UG1.grph_domain_code =? ");
        }
        strBuilder.append(")) ");
        
        final String sqlSelect = strBuilder.toString();
        
        LogFactory.printLog("PointEnquiryPostgresQry", sqlSelect, LOG);
        
        int i = 0;
        PreparedStatement pstmt = con.prepareStatement(sqlSelect);

        pstmt.setString(++i, networkCode);
        if (!(PretupsI.ALL).equals(domain)) {
            if ((PretupsI.ALL).equals(categoryCode)) {
                pstmt.setString(++i, domain);
            } else {
                pstmt.setString(++i, categoryCode);
            }
        }
        pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
        pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
        pstmt.setString(++i, PretupsI.USER_TYPE_CHANNEL);
        pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
        if (!(PretupsI.ALL).equals(geographicalDomainCode)) {
            pstmt.setString(++i, geographicalDomainCode);
        }


		
		return pstmt;
	}

}
