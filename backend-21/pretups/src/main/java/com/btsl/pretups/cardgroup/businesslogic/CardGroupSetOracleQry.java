package com.btsl.pretups.cardgroup.businesslogic;

public class CardGroupSetOracleQry implements CardGroupSetQry{
	@Override
	public String loadCardGroupVersionCacheQry(){
		final StringBuilder qry = new StringBuilder("SELECT cgsv.card_group_set_id, cgsv.version, cgsv.applicable_from ");
        qry.append("FROM card_group_set_versions cgsv ,card_group_set cgs ");
        qry.append("WHERE cgs.card_group_set_id=cgsv.card_group_set_id ");
        qry.append("AND (cgsv.applicable_from >=(SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme ");
        qry.append("WHERE  cdme.applicable_from<=SYSDATE AND cdme.card_group_set_id=cgs.card_group_set_id) OR cgsv.applicable_from >=sysdate) ");
        qry.append("ORDER BY card_group_set_id,version ");
        return qry.toString();
		
	}

}
