package com.btsl.common;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileRestServiceImpl;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportRestServiceImpl;
import com.btsl.pretups.channel.transfer.businesslogic.C2SReversalRestServiceImpl;
import com.btsl.pretups.channel.transfer.requesthandler.AutoCompleteUserDetailsController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CReturnController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CTransferMultController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CTrfInitiateController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CUserBuyEnquiryController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CWithdrawController;
import com.btsl.pretups.channel.transfer.requesthandler.C2STotalNoOfTransactionController;
import com.btsl.pretups.channel.transfer.requesthandler.C2STransactionController;
import com.btsl.pretups.channel.transfer.requesthandler.C2STransferServiceTotalAmountController;
import com.btsl.pretups.channel.transfer.requesthandler.CommissionCalculatorController;
import com.btsl.pretups.channel.transfer.requesthandler.GetDomainCategoryController;
import com.btsl.pretups.channel.transfer.requesthandler.PassbookDetailsController;
import com.btsl.pretups.channel.transfer.requesthandler.SendOtpForForgotPinController;
import com.btsl.pretups.channel.transfer.requesthandler.SenderReceiverDetailsController;
import com.btsl.pretups.channel.transfer.requesthandler.TotalTransactionsDetailedView;
import com.btsl.pretups.channel.transfer.requesthandler.TotalUserIncomeDetailsViewController;
import com.btsl.pretups.channel.transfer.requesthandler.TransactionAPICalculationController;
import com.btsl.pretups.channel.transfer.requesthandler.UserPaymentTypesController;
import com.btsl.pretups.channel.user.businesslogic.MobileAppRestServiceImpl;
import com.btsl.pretups.channel.userreturn.businesslogic.C2CWithdrawRestServiceImpl;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionRestServiceImpl;
import com.btsl.pretups.domain.businesslogic.CategoryRestServiceImpl;
import com.btsl.pretups.interfaces.businesslogic.InterfaceManagementRestServiceImpl;
import com.btsl.pretups.lowbase.businesslogic.LowBaseTxnEnqRestServiceImpl;
import com.btsl.pretups.master.businesslogic.LookupsRestServiceImpl;
import com.btsl.pretups.master.businesslogic.NetworkSummaryReportRestServiceImpl;
import com.btsl.pretups.network.businesslogic.ChangeNetworkRestServiceImpl;
import com.btsl.pretups.network.businesslogic.NetworkStatusRestServiceImpl;
import com.btsl.pretups.network.businesslogic.ShowNetworkRestServiceImpl;
//import com.btsl.pretups.network.businesslogic.ShowNetworkRestServiceImpl;
import com.btsl.pretups.network.businesslogic.ViewNetworkRestServiceImpl;
import com.btsl.pretups.restrictedsubs.businesslogic.BatchRechargeRescheduleRestServiceImpl;
import com.btsl.pretups.restrictedsubs.businesslogic.BatchScheduleRechargeRestServiceImpl;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduleRechargeRestServiceImpl;
import com.btsl.pretups.restrictedsubs.businesslogic.ViewScRCBatchRestServiceImpl;
import com.btsl.pretups.restrictedsubs.businesslogic.ViewScheduleTopupRestServiceImpl;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceRestImpl;
import com.btsl.pretups.subscriber.businesslogic.BarredUserRestServiceImpl;
import com.btsl.pretups.user.requesthandler.ChannelUserDetailsController;
import com.btsl.pretups.user.requesthandler.OtpValidationandPinUpdation;
import com.btsl.user.businesslogic.ViewSelfDetailsRestServiceImpl;
import com.restapi.c2sservices.controller.C2SBulkRechargeController;
import com.restapi.c2sservices.service.C2SServicesRestController;
import com.restapi.cardgroup.service.AddCardGroup;
import com.restapi.cardgroup.service.AddP2PCardGroup;
import com.restapi.cardgroup.service.CalculateVoucherCardGroup;
import com.restapi.cardgroup.service.CardGroupChangeStatus;
import com.restapi.cardgroup.service.DefaultCardGroup;
import com.restapi.cardgroup.service.ViewCardGroup;
import com.restapi.channel.transfer.channelvoucherapproval.ChannelToChannelTransferApproavlListController;
import com.restapi.channelenquiry.service.ChannelEnquiryRestController;
import com.restapi.networkstock.service.NetworkStockCreation;
import com.restapi.o2c.service.FOCInitiateController;
import com.restapi.p2penquiry.service.P2PEnquiryRestController;
import com.restapi.user.service.C2CBuyVoucherController;
import com.restapi.user.service.C2CDownloadFileController;
import com.restapi.user.service.C2CFileUploadController;
import com.restapi.user.service.C2CStockApprovalController;
import com.restapi.user.service.C2CStockTransferController;
import com.restapi.user.service.C2CTransferDetailController;
import com.restapi.user.service.C2CVoucherApprvlController;
import com.restapi.user.service.C2CVoucherTransferController;
import com.restapi.user.service.ChannelUserServices;
import com.restapi.user.service.RegularExpressionController;
import com.restapi.user.service.SuspendResumeRestController;
import com.restapi.user.service.UserPassbookController;
import com.restapi.user.service.VoucherInfoServices;
import com.restapi.voucherbundle.service.AddVoucherBundle;
import com.restapi.voucherbundle.service.ModifyVoucherBundle;

/**
 * This class registers the Rest Services
 * @author lalit.chattar
 *
 */
public class PretupsApplication extends Application {

	private final Set<Object> singleton = new HashSet<>();

	/**
	 * This constructor register rest services in singleton object
	 */
	public PretupsApplication() {
		singleton.add(new RestController());
		singleton.add(new LookupsRestServiceImpl());
		singleton.add(new BarredUserRestServiceImpl());
		singleton.add(new RestAllExceptionHandler());
		singleton.add(new RestNullPointerException());
		singleton.add(new CommissionProfileRestServiceImpl());
		singleton.add(new InterfaceManagementRestServiceImpl());
		singleton.add(new C2SReversalRestServiceImpl());
		singleton.add(new NetworkSummaryReportRestServiceImpl());
		singleton.add(new CurrencyConversionRestServiceImpl());
		singleton.add(new LowBaseTxnEnqRestServiceImpl());
		singleton.add(new C2CWithdrawRestServiceImpl());
		singleton.add(new ViewNetworkRestServiceImpl());
		singleton.add(new ShowNetworkRestServiceImpl());
		singleton.add(new NetworkStatusRestServiceImpl());
		singleton.add(new ChangeNetworkRestServiceImpl());
		//singleton.add(new ApprovalUserDeleteSuspendRestServiceImpl());
		singleton.add(new CategoryRestServiceImpl());
		singleton.add(new ViewScheduleTopupRestServiceImpl());
		singleton.add(new ScheduleRechargeRestServiceImpl());
		singleton.add(new ViewScRCBatchRestServiceImpl());
		singleton.add(new BatchRechargeRescheduleRestServiceImpl());
		singleton.add(new BatchScheduleRechargeRestServiceImpl());
		singleton.add(new ServiceRestImpl());
		singleton.add(new ViewSelfDetailsRestServiceImpl());
		//singleton.add(new BulkVoucherResendPinRestServiceImpl());
		singleton.add(new ChannelUserReportRestServiceImpl());
		singleton.add(new RestReceiver());
		singleton.add(new MobileAppRestServiceImpl());
		singleton.add(new CardGroupChangeStatus());
		singleton.add(new ViewCardGroup());
		singleton.add(new DefaultCardGroup());
		singleton.add(new NetworkStockCreation());
		singleton.add(new AddCardGroup()); 
		singleton.add(new CalculateVoucherCardGroup());
		
		singleton.add(new ChannelUserServices());
		
		singleton.add(new C2SServicesRestController());
		singleton.add(new ChannelEnquiryRestController());
		singleton.add(new P2PEnquiryRestController());
		singleton.add(new AddVoucherBundle());
		singleton.add(new ModifyVoucherBundle());
		singleton.add(new C2CVoucherApprvlController());
		singleton.add(new C2CTransferDetailController());
		singleton.add(new C2CStockApprovalController());
		singleton.add(new ChannelToChannelTransferApproavlListController());
		singleton.add(new C2CBuyVoucherController());
		singleton.add(new C2CVoucherTransferController());
		singleton.add(new C2CDownloadFileController());
		singleton.add(new C2CFileUploadController());
		singleton.add(new C2CStockTransferController());
		singleton.add(new VoucherInfoServices());
		singleton.add(new ChannelUserDetailsController());
		
		singleton.add(new C2CUserBuyEnquiryController());

		singleton.add(new UserPassbookController());
		singleton.add(new CommissionCalculatorController());
		singleton.add(new C2STransferServiceTotalAmountController());
		singleton.add(new PassbookDetailsController());
		singleton.add(new C2STransactionController());

		singleton.add(new C2STotalNoOfTransactionController());
		singleton.add(new TotalUserIncomeDetailsViewController());
		singleton.add(new SendOtpForForgotPinController());
		singleton.add(new OtpValidationandPinUpdation());
		singleton.add(new TotalTransactionsDetailedView());
		singleton.add(new TransactionAPICalculationController());
		
		singleton.add(new UserPaymentTypesController());
		singleton.add(new GetDomainCategoryController());
		singleton.add(new AutoCompleteUserDetailsController());
		singleton.add(new SenderReceiverDetailsController());
		singleton.add(new AddP2PCardGroup()); 
		singleton.add(new C2CReturnController());
		singleton.add(new C2CWithdrawController());
		singleton.add(new C2CTrfInitiateController());
		singleton.add(new C2CTransferMultController());
		singleton.add(new RegularExpressionController());
		singleton.add(new C2SBulkRechargeController());
		singleton.add(new FOCInitiateController());
		singleton.add(new SuspendResumeRestController());
	}

	@Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        //set.add(io.swagger.jaxrs.listing.ApiListingResource.class);
		//set.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return set;
    }
	
	@Override
	public Set<Object> getSingletons() {
		return singleton;
	}
}

