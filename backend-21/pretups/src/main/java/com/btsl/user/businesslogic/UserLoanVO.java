package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class UserLoanVO implements Serializable, Cloneable{
	
	
	private String user_id;
	public UserLoanVO(String user_id, int profile_id, String product_code, long loan_threhold, long loan_amount,
			String loan_given, long loan_given_amount, Date last_loan_date, String last_loan_txn_id,
			String settlement_id, Date settlement_date, long settlement_loan_amount, long settlement_loan_interest,
			String loan_taken_from, String settlement_from, String optinout_allowed, Date optinout_on,
			String optinout_by,long balance_before_loan, long calculatedPremium, long totalAmountDue, String settlementStatus,
			String loanEligibility) {
		super();
		this.user_id = user_id;
		this.profile_id = profile_id;
		this.product_code = product_code;
		this.loan_threhold = loan_threhold;
		this.loan_amount = loan_amount;
		this.loan_given = loan_given;
		this.loan_given_amount = loan_given_amount;
		this.last_loan_date = last_loan_date;
		this.last_loan_txn_id = last_loan_txn_id;
		this.settlement_id = settlement_id;
		this.settlement_date = settlement_date;
		this.settlement_loan_amount = settlement_loan_amount;
		this.settlement_loan_interest = settlement_loan_interest;
		this.loan_taken_from = loan_taken_from;
		this.settlement_from = settlement_from;
		this.optinout_allowed = optinout_allowed;
		this.optinout_on = optinout_on;
		this.optinout_by = optinout_by;
		this.balance_before_loan=balance_before_loan;
		this.calculatedPremium=calculatedPremium;
		this.totalAmountDue=totalAmountDue;
		this.settlementStatus=settlementStatus;
		this.loanEligibility=loanEligibility;
	}
	public UserLoanVO() {
		// TODO Auto-generated constructor stub
	}
	private int profile_id;
	private String product_code;
	
	private long  loan_threhold;
	private long  loan_amount;
	private String loan_given;
	private long  loan_given_amount;
	private Date  last_loan_date;
	private String last_loan_txn_id;
	private String settlement_id;
	private Date settlement_date;
	private long  settlement_loan_amount;
	private long  settlement_loan_interest;
	private String loan_taken_from;
	private String settlement_from;
	private String optinout_allowed;
	private Date  optinout_on;
	private String optinout_by;
	private long  balance_before_loan;
	
	private long calculatedPremium;
	private long totalAmountDue;
	private String settlementStatus;
	private String loanEligibility;
	
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getProfile_id() {
		return profile_id;
	}
	public void setProfile_id(int profile_id) {
		this.profile_id = profile_id;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public long getLoan_threhold() {
		return loan_threhold;
	}
	public void setLoan_threhold(long loan_threhold) {
		this.loan_threhold = loan_threhold;
	}
	public long getLoan_amount() {
		return loan_amount;
	}
	public void setLoan_amount(long loan_amount) {
		this.loan_amount = loan_amount;
	}
	public String getLoan_given() {
		return loan_given;
	}
	public void setLoan_given(String loan_given) {
		this.loan_given = loan_given;
	}
	public long getLoan_given_amount() {
		return loan_given_amount;
	}
	public void setLoan_given_amount(long loan_given_amount) {
		this.loan_given_amount = loan_given_amount;
	}
	public Date getLast_loan_date() {
		return last_loan_date;
	}
	public void setLast_loan_date(Date last_loan_date) {
		this.last_loan_date = last_loan_date;
	}
	public String getLast_loan_txn_id() {
		return last_loan_txn_id;
	}
	public void setLast_loan_txn_id(String last_loan_txn_id) {
		this.last_loan_txn_id = last_loan_txn_id;
	}
	public String getSettlement_id() {
		return settlement_id;
	}
	public void setSettlement_id(String settlement_id) {
		this.settlement_id = settlement_id;
	}
	public Date getSettlement_date() {
		return settlement_date;
	}
	public void setSettlement_date(Date settlement_date) {
		this.settlement_date = settlement_date;
	}
	public long getSettlement_loan_amount() {
		return settlement_loan_amount;
	}
	public void setSettlement_loan_amount(long settlement_loan_amount) {
		this.settlement_loan_amount = settlement_loan_amount;
	}
	public long getSettlement_loan_interest() {
		return settlement_loan_interest;
	}
	public void setSettlement_loan_interest(long settlement_loan_interest) {
		this.settlement_loan_interest = settlement_loan_interest;
	}
	public String getLoan_taken_from() {
		return loan_taken_from;
	}
	public void setLoan_taken_from(String loan_taken_from) {
		this.loan_taken_from = loan_taken_from;
	}
	public String getSettlement_from() {
		return settlement_from;
	}
	public void setSettlement_from(String settlement_from) {
		this.settlement_from = settlement_from;
	}
	public String getOptinout_allowed() {
		return optinout_allowed;
	}
	public void setOptinout_allowed(String optinout_allowed) {
		this.optinout_allowed = optinout_allowed;
	}
	public Date getOptinout_on() {
		return optinout_on;
	}
	public void setOptinout_on(Date optinout_on) {
		this.optinout_on = optinout_on;
	}
	public String getOptinout_by() {
		return optinout_by;
	}
	public void setOptinout_by(String optinout_by) {
		this.optinout_by = optinout_by;
	}
	
	public long getBalance_before_loan() {
		return balance_before_loan;
	}
	public void setBalance_before_loan(long balance_before_loan) {
		this.balance_before_loan = balance_before_loan;
	}
	
	
	
	public long getCalculatedPremium() {
		return calculatedPremium;
	}
	public void setCalculatedPremium(long calculatedPremium) {
		this.calculatedPremium = calculatedPremium;
	}
	public long getTotalAmountDue() {
		return totalAmountDue;
	}
	public void setTotalAmountDue(long totalAmountDue) {
		this.totalAmountDue = totalAmountDue;
	}
	public String getSettlementStatus() {
		return settlementStatus;
	}
	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	public String getLoanEligibility() {
		return loanEligibility;
	}
	public void setLoanEligibility(String loanEligibility) {
		this.loanEligibility = loanEligibility;
	}
	@Override
	public String toString() {
		final StringBuffer sbf = new StringBuffer();
        sbf.append("UserLoanVO [user_id=");
        sbf.append( user_id  );
        sbf.append( ", profile_id="  );
        sbf.append( profile_id  );
        sbf.append( ", product_code="  );
        sbf.append( product_code);
        sbf.append( ", loan_threhold="  );
        sbf.append( loan_threhold  );
        sbf.append( ", loan_amount="  );
        sbf.append( loan_amount  );
        sbf.append( ", loan_given="  );
        sbf.append( loan_given);
        sbf.append( ", loan_given_amount="  );
        sbf.append( loan_given_amount  );
        sbf.append( ", last_loan_date="  );
        sbf.append( last_loan_date);
        sbf.append( ", last_loan_txn_id="  );
        sbf.append( last_loan_txn_id  );
        sbf.append( ", settlement_id="  );
        sbf.append( settlement_id  );
        sbf.append( ", settlement_date=");
        sbf.append( settlement_date  );
        sbf.append( ", settlement_loan_amount="  );
        sbf.append( settlement_loan_amount  );
        sbf.append( ", settlement_loan_interest=");
        sbf.append( settlement_loan_interest  );
        sbf.append( ", loan_taken_from="  );
        sbf.append( loan_taken_from  );
        sbf.append( ", settlement_from=");
        sbf.append( settlement_from  );
        sbf.append( ", optinout_allowed="  );
        sbf.append( optinout_allowed  );
        sbf.append( ", optinout_on="  );
        sbf.append( optinout_on);
        sbf.append( ", optinout_by="  );
        sbf.append( optinout_by  );
        sbf.append( ", balance_before_loan="  );
        sbf.append( balance_before_loan  );
        sbf.append( ", calculatedPremium="  );
        sbf.append( calculatedPremium  );
        sbf.append( ", totalAmountDue="  );
        sbf.append( totalAmountDue  );
        sbf.append( ", settlementStatus="  );
        sbf.append( settlementStatus  );
        sbf.append( ", loanEligibility="  );
        sbf.append( loanEligibility  );
        sbf.append( "]");
        
        return sbf.toString();
        
	}
}
