package com.inter.zteoml;

import zsmart.ztesoft.com.xsd.TRechargingBenefitDto;
import zsmart.ztesoft.com.xsd.TRechargingRequest;

public class TestMain {
    
    
    public TRechargingRequest generateRechargingRequest()
    {
        TRechargingRequest rechargingRequest =new TRechargingRequest();
        
        rechargingRequest.setTransactionSN("20150630001");
        rechargingRequest.setMSISDN("22394880987");
        rechargingRequest.setAcctResCode("1");
        rechargingRequest.setAddBalance("-100");
        rechargingRequest.setAddDays(10);
        TRechargingBenefitDto[] benefitDto=new TRechargingBenefitDto[10]; 

         
        //TArrayOfRechargingBenefitDto[] BalDtoList=new TArrayOfRechargingBenefitDto[20];
        TRechargingBenefitDto benefitDto2 =new TRechargingBenefitDto();
        if(benefitDto!=null){
        for (int i=0;i<4;i++)
        {
            benefitDto[0]=new TRechargingBenefitDto();
            benefitDto[0].setAcctResCode("0");
            benefitDto2.setAcctResCode(String.valueOf(i+1));
            benefitDto2.setAddBalance(String.valueOf("10"));
        }
        }
        rechargingRequest.setBenefitDtoList(benefitDto);
    
        StringBuffer buffer =new StringBuffer();
        buffer.append("<soap:Header>");
        buffer.append("<xsd:AuthHeader> ");
        buffer.append("<Username>ocstest</Username> ");
        buffer.append("<Password>smart</Password> ");
        buffer.append("</xsd:AuthHeader> ");
        buffer.append("</soap:Header> ");
        buffer.append("<soap:Body> ");
        buffer.append("<xsd:RechargingRequest> ");
        buffer.append("<MSISDN>"+rechargingRequest.getMSISDN()+"</MSISDN> ");
        buffer.append("<TransactionSN>"+rechargingRequest.getTransactionSN()+"</TransactionSN> ");
        buffer.append("<AcctResCode>"+rechargingRequest.getAcctResCode()+"</AcctResCode> ");
        buffer.append("<AddBalance>"+rechargingRequest.getAddBalance()+"</AddBalance> ");
        buffer.append("<AddDays>"+rechargingRequest.getAddDays()+"</AddDays> ");
        if(rechargingRequest.getBenefitDtoList().length>0)
        {
            buffer.append("<BalDtoList>");
            for(int i=0;i<rechargingRequest.getBenefitDtoList().length;i++)
            {
                buffer.append("<BalDto>");
                buffer.append("<AcctResCode>"+rechargingRequest.getBenefitDtoList()[i].getAcctResCode()+"</AcctResCode>");
                buffer.append("<Balance>-"+rechargingRequest.getBenefitDtoList()[i].getAddBalance()+"</Balance>");
                buffer.append("</BalDto>");
            }
            buffer.append("</BalDtoList>");
        }
        buffer.append("</xsd:RechargingRequest>");
        buffer.append("</soap:Body>");
           
           System.out.println("recharging Object String :: "+buffer.toString());
        
        return rechargingRequest;
        
    }
    
public static void main(String[] args) {
    
    new TestMain().generateRechargingRequest();
    
}

}
