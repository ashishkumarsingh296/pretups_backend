package com.btsl.db.query.oracle;

import com.btsl.pretups.channel.transfer.requesthandler.VOMSSnifferQry;

/**
 * VOMSSnifferOracleQry
 * @author sadhan.k
 *
 */
public class VOMSSnifferOracleQry implements VOMSSnifferQry{

	@Override
	public String fillData(int count) {
		
		StringBuilder selectBuff = null;
		
		if (count == 0 ){
			selectBuff = new StringBuilder(" insert into VOMS_VOUCHERS_SNIFFER (select * from voms_vouchers where product_id=? and mrp=? and current_status=? "
					+ "and subscriber_id is null  and rownum<? ) ");
		}
		else {
			selectBuff = new StringBuilder(" insert into VOMS_VOUCHERS_SNIFFER (select * from voms_vouchers where product_id=? and mrp=? and current_status=? and "
					+ "subscriber_id is null and serial_no>(select max(serial_no) from VOMS_VOUCHERS_SNIFFER where product_id=? and mrp=?)  and rownum<? ) ");
		}
		
		return selectBuff.toString();
	}

}
