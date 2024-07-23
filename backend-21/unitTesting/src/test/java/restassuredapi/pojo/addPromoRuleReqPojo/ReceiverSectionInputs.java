package restassuredapi.pojo.addPromoRuleReqPojo;

public class ReceiverSectionInputs {
	
	
	
	//Receiver Details
			private String rowIndex;
			private String Type;
			private String serviceClassID;
			private String subscriberStatusValue;
			
			// card group set
			private String serviceCardGroupID;
			private String status;
			private String subservice;
			private String serviceType;
			private String cardGroupSet;
			
			//
			private String applicableFrom;
			private String applicableTo;
			private String timeSlabs;
			
			public ReceiverSectionInputs(){
				
			}
			
		
			public String getType() {
				return Type;
			}
			public void setType(String type) {
				Type = type;
			}
			public String getServiceClassID() {
				return serviceClassID;
			}
			public void setServiceClassID(String serviceClassID) {
				this.serviceClassID = serviceClassID;
			}
			public String getSubscriberStatusValue() {
				return subscriberStatusValue;
			}
			public void setSubscriberStatusValue(String subscriberStatusValue) {
				this.subscriberStatusValue = subscriberStatusValue;
			}
			public String getServiceCardGroupID() {
				return serviceCardGroupID;
			}
			public void setServiceCardGroupID(String serviceCardGroupID) {
				this.serviceCardGroupID = serviceCardGroupID;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			public String getSubservice() {
				return subservice;
			}
			public void setSubservice(String subservice) {
				this.subservice = subservice;
			}
			public String getServiceType() {
				return serviceType;
			}
			public void setServiceType(String serviceType) {
				this.serviceType = serviceType;
			}
			public String getCardGroupSet() {
				return cardGroupSet;
			}
			public void setCardGroupSet(String cardGroupSet) {
				this.cardGroupSet = cardGroupSet;
			}
			public String getApplicableFrom() {
				return applicableFrom;
			}
			public void setApplicableFrom(String applicableFrom) {
				this.applicableFrom = applicableFrom;
			}
			public String getApplicableTo() {
				return applicableTo;
			}
			public void setApplicableTo(String applicableTo) {
				this.applicableTo = applicableTo;
			}
			public String getTimeSlabs() {
				return timeSlabs;
			}
			public void setTimeSlabs(String timeSlabs) {
				this.timeSlabs = timeSlabs;
			}
			public String getRowIndex() {
				return rowIndex;
			}
			public void setRowIndex(String rowIndex) {
				this.rowIndex = rowIndex;
			}


}
