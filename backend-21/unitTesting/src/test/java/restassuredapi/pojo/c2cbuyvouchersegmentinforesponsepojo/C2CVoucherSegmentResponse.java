package restassuredapi.pojo.c2cbuyvouchersegmentinforesponsepojo;

public class C2CVoucherSegmentResponse {

	String voucherSegmentcode;
	String voucherSegmentValue;
	
	public String getCode() {
		return voucherSegmentcode;
	}
	public void setCode(String code) {
		this.voucherSegmentcode = code;
	}
	public String getValue() {
		return voucherSegmentValue;
	}
	public void setValue(String value) {
		this.voucherSegmentValue = value;
	}
	
	@Override
	public String toString() {
		return "{key=" + voucherSegmentcode + ", value=" + voucherSegmentValue + "}";
	}
	

}
