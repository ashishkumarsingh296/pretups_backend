package com.btsl.db.query.postgres;

import com.btsl.pretups.channel.transfer.requesthandler.VOMSSnifferQry;

/**
 * VOMSSnifferPostgresQry
 * @author sadhan.k
 *
 */
public class VOMSSnifferPostgresQry implements VOMSSnifferQry {

	@Override
	public String fillData(int count) {
		
		StringBuilder selectBuff = null;
		
		if (count == 0 ){
			selectBuff = new StringBuilder(" insert into VOMS_VOUCHERS_SNIFFER (select * from voms_vouchers where product_id=? and mrp=? and current_status=? "
					+ "and subscriber_id is null limit ?-1 ) ");
		}
		else {
			selectBuff = new StringBuilder(" insert into VOMS_VOUCHERS_SNIFFER (select * from voms_vouchers where product_id=? and mrp=? and current_status=? and "
					+ "subscriber_id is null and serial_no>(select max(serial_no) from VOMS_VOUCHERS_SNIFFER where product_id=? and mrp=?) limit ?-1 ) ");
		}
		
		return selectBuff.toString();
	}

}
