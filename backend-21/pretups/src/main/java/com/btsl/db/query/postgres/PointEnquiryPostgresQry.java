package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.pointenquiry.businesslogic.PointEnquiryQry;


/**
 * 
 * @author sadhan.k
 *PointEnquiryPostgresQry
 */
public class PointEnquiryPostgresQry implements PointEnquiryQry{

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

        strBuilder.append(" IN (WITH RECURSIVE q AS (");
        strBuilder.append(" SELECT grph_domain_code ,status FROM geographical_domains  WHERE ");
        strBuilder.append(" grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1");
        if (!(PretupsI.ALL).equals(geographicalDomainCode)) {
            strBuilder.append(" WHERE UG1.grph_domain_code =? ");
        }
        strBuilder.append(") ");
        strBuilder.append("UNION ALL");
        strBuilder.append(" SELECT gd.grph_domain_code,gd.status FROM geographical_domains gd");
        strBuilder.append(" JOIN q ON  q.grph_domain_code = gd.parent_grph_domain_code");
        strBuilder.append(" ) SELECT grph_domain_code from q where status =? ) ");
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
        if (!(PretupsI.ALL).equals(geographicalDomainCode)) {
            pstmt.setString(++i, geographicalDomainCode);
        }      
        pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);

		
		return pstmt;
	}


}
