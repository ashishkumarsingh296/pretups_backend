package com;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.btsl.pretups.rest.client.RootApplicationConfig;
import com.btsl.util.Constants;
import com.restapi.oauth.services.OAuthenticationController;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

//import org.apache.logging.log4j.util.ServiceLoaderUtil;
//import org.apache.struts.action.ActionServlet;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.*;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.C2SSubscriberReceiver;
import com.btsl.pretups.channel.receiver.ChannelReceiver;
import com.btsl.pretups.channel.receiver.VomsReciever;
import com.btsl.pretups.common.BasicCaptchaServlet;
import com.btsl.pretups.filters.LoadSalt;
import com.btsl.pretups.filters.ParamFilter;
import com.btsl.pretups.filters.ParamWrapperFilter;
import com.btsl.pretups.filters.ValidateSalt;
import com.btsl.security.EncodingFilter;
import com.btsl.user.businesslogic.LookupCache;
import com.btsl.user.businesslogic.MessageGatewayCache;
import com.btsl.user.businesslogic.MessageGatewayRedisCache;
import com.btsl.user.businesslogic.NetworkPreferenceCache;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.SysPrefService;
import com.btsl.user.businesslogic.SysPreferenceCache;
import com.btsl.user.businesslogic.VMSCacheRepository;
import com.btsl.user.businesslogic.VMSLocaleResolver;
import com.btsl.util.ConfigServlet;
import com.captcha.botdetect.web.servlet.SimpleCaptchaServlet;
import com.google.common.collect.Lists;
//import com.inter.comverse.ComverseTestServer;
import com.restapi.oauth.services.CustomHandlerInterceptor;
import com.restapi.oauth.services.NonceInterceptor;
import com.btsl.pretups.common.UpdateCacheServlet;
import com.btsl.pretups.common.UpdateRedisCacheServlet;

import io.micrometer.core.instrument.MeterRegistry;
/*
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
*/
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;


@RestController
//at org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
@SpringBootApplication//(exclude = { WebMvcAutoConfiguration.class})

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableWebMvc
@Configuration
@PropertySource(value="classpath:swagger.yml", encoding="UTF-8")
@Import({ RootApplicationConfig.class })
//@EnableSwagger2
@EnableAsync

@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
//@EnableJpaRepositories("com.delivery.repository")
@EntityScan(basePackages = {"com.btsl.user.businesslogic", "com.restapi.oauth.services", "com.btsl.user.businesslogic.entity"})
@EnableJpaRepositories(basePackages = {"com.btsl.user.businesslogic", "com.restapi.oauth.services"} )
@ComponentScan(basePackages = { "com.btsl.security", "com.btsl.common", "com.restapi", "com.restapi.user.service", "com.btsl.pretups.channel.transfer.requesthandler" , "com.btsl.user.businesslogic", "com.restapi.oauth.services","com.restapi.cardgroup.service"})
public class PretupsStarter /*extends SpringBootServletInitializer*/ extends
		WebMvcConfigurationSupport implements  /*WebMvcConfigurer,*/
		EnvironmentAware,CommandLineRunner{




	private static Log _logger = LogFactory.getLog(PretupsStarter.class.getName());

	private static ConfigurableApplicationContext context;

	@Autowired
	private ApplicationContext appContext;

	@Value("${ALLOWED_ORIGINS}")
	String allowedOrigins;


	@GetMapping("hello")
	public String hello() {
		return "hello";
	}

	@Autowired
	private SysPrefService systePrefService;

	@Autowired
	private VMSCacheRepository vmsCacheRepoistory;

	private static Environment environment;
	@Value("${threadpool.corepoolsize}")
	int corePoolSize;

	@Value("${threadpool.maxpoolsize}")
	int maxPoolSize;



	@Value("${PreTUPS_Desc}")
	String preTUPS_Desc;
 /*   @Autowired
    CustomHandlerInterceptor customHandlerInterceptor;*/

/*    @Autowired
    private NonceInterceptor nonceInterceptor;
*/


	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}



	//




	//

	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
	webServerFactoryCustomizer() {


		return factory -> factory.setContextPath("/pretups");
	}

	@Bean
	public FilterRegistrationBean encodingFilterBean() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(encodingFilter());
		registration.addUrlPatterns("/*");

		registration.addInitParameter("encoding", "UTF-8");
		registration.addInitParameter("forceEncoding", "false");
		registration.setName("encodingFilter");

		registration.setOrder(1);
		return registration;
	}


	/*@Bean
	public ServletRegistrationBean restEasyService() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher(), "/rest/*");

		Map<String,String> params = new HashMap<String,String>();
		params.put("javax.ws.rs.Application", "com.btsl.common.PretupsApplication");

		bean.setInitParameters(params);
		return bean;
	}*/
	/*@Bean
	ServletRegistrationBean captchaServletRegistration () {
		ServletRegistrationBean srb = new ServletRegistrationBean();
		srb.setServlet(new SimpleCaptchaServlet());
		srb.addUrlMappings("/simple-captcha-endpoint");
		return srb;
	}*/
	@Bean
	ServletRegistrationBean captchaServletRegistration1 () {
		ServletRegistrationBean srb = new ServletRegistrationBean();
		srb.setServlet(new BasicCaptchaServlet());
		srb.addUrlMappings("/captchaBasic");
		return srb;
	}
	@Bean
	public ServletContextInitializer initializer1() {
		return new ServletContextInitializer() {

			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.setInitParameter(
						"BDC_configFileLocation", "/WEB-INF/botdetect.xml");
			}
		};
	}
	@Bean
	public ServletContextInitializer initializer() {

		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.setInitParameter("resteasy.servlet.mapping.prefix", "/rest");
			}
		};
	}


	public Filter encodingFilter() {
		return new EncodingFilter();
	}

	@Bean
	public ServletRegistrationBean exampleServletBean() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new ConfigServlet(), "/abcd");

		//System.getProperty("catalina.base");

		Map<String,String> params = new HashMap<String,String>();

		//params.put("constantspropsfile9", "/WEB-INF/classes/configfiles/Constants.props");
		//params.put("loggerConfigFile9","configfiles/LogConfig.props");
		//params.put("kafkapropsfile","/WEB-INF/classes/configfiles/KafkaConstants.props");
		params.put("securityconstantspropsfile","configfiles/SecurityConstants.props");
		params.put("restfulconstantspropsfile","configfiles/RestfulConstants.props");
		params.put("clientblconstantspropsfile","configfiles/ClientBLConstants.props");
		params.put("postgresqueryconstantspropsfile","configfiles/queryConstants/postgresqueryconstants.props");
		params.put("oraclequeryconstantspropsfile","configfiles/queryConstants/oraclequeryconstants.props");
		bean.setInitParameters(params);
		bean.setLoadOnStartup(1);
		return bean;
	}


	@Bean
	public ServletRegistrationBean c2SReceiver() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new ChannelReceiver(), "/C2SReceiver");


		return bean;
	}

/*

	@Bean
	public ServletRegistrationBean p2pReceiver() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new P2PReceiver(), "/P2PReceiver");


	    return bean;
	}

	@Bean
	public ServletRegistrationBean C2SSubscriberReceiver() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new C2SSubscriberReceiver(), "/C2SSubscriberReceiver");


	    return bean;
	}



	@Bean
	public ServletRegistrationBean SystemReceiver() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new SystemReceiver(), "/SystemReceiver");

	    return bean;
	}


	@Bean
	public ServletRegistrationBean ExtGWChannelReceiver() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ExtGWChannelReceiver(), "/ExtGWChannelReceiver");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("instanceCode","10");

	    return bean;
	}

	*/
	/*@Bean
	public ServletRegistrationBean FermaTestServer() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new FermaTestServer(), "/FermaTestServer");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("fermaxmlfilepath","/WEB-INF/classes/configfiles/INFiles/FermaRequestResponse.props");


	    return bean;
	}
	*/

/*	@Bean
	public ServletRegistrationBean FermaConnectionServlet() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new FermaConnectionServlet(), "/FermaConnectionServlet");

	    return bean;
	}*/


/*	@Bean
	public ServletRegistrationBean PostPaidTestServlet() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new PostPaidTestServlet(), "/PostPaidTestServlet");

	    return bean;
	}*/

/*	@Bean
	public ServletRegistrationBean AlcatelRequestInitiator() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new AlcatelRequestInitiator(), "");

	    bean.setLoadOnStartup(1);
	    return bean;
	}

	*/

/*	@Bean
	public ServletRegistrationBean InterfaceCloserServlet() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new InterfaceCloserServlet(), "/InterfaceCloserServlet");

	    bean.setLoadOnStartup(5);
	    return bean;
	}
	*/
/*	@Bean
	public ServletRegistrationBean NodeServlet1() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new NodeServle(), "/NodeServlet");

	    bean.setLoadOnStartup(2);
	    return bean;
	}

	@Bean
	public ServletRegistrationBean NodeServlet2() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new NodeServlet(), "/NodeServlet");

	    bean.setLoadOnStartup(2);
	    return bean;
	}

	@Bean
	public ServletRegistrationBean NodeServletSAP() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new NodeServletSAP(), "");

	    bean.setLoadOnStartup(2);
	    return bean;
	}
	*/
/*	@Bean
	public ServletRegistrationBean downloadUtil() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new downloadUtil(), "/downloadUtil");


	    return bean;
	}

	@Bean
	public ServletRegistrationBean ImageCaptchaServlet() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ImageCaptchaServlet(), "/ImageCaptchaServlet");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("captcha-width","300");
	    params.put("captcha-height","100");


	    return bean;
	}
	*/
	/*@Bean
	public ServletRegistrationBean PaymentGatewayTestServer() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new PaymentGatewayTestServer(), "/PaymentGatewayTestServer");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("responseFilePath","/WEB-INF/classes/configfiles/INFiles/pgResponse.props");



	    return bean;
	}

	@Bean
	public ServletRegistrationBean DownloadTemplateUtil() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new DownloadTemplateUtil(), "/DownloadTemplateUtil");

	    bean.setLoadOnStartup(2);
	    return bean;
	}
*/

	@Bean
	public ServletRegistrationBean UpdateCacheServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new UpdateCacheServlet(), "/UpdateCacheServlet");

		Map<String,String> params = new HashMap<String,String>();
		params.put("constantspropsfile","/WEB-INF/classes/configfiles/Constants.props");
		params.put("loggerConfigFile","/WEB-INF/classes/configfiles/LogConfig.props");
		params.put("instanceCode","2");

		return bean;
	}

	@Bean
	public ServletRegistrationBean UpdateRedisCacheServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new UpdateRedisCacheServlet(), "/UpdateRedisCacheServlet");

		Map<String,String> params = new HashMap<String,String>();
		params.put("constantspropsfile","/WEB-INF/classes/configfiles/Constants.props");
		params.put("loggerConfigFile","/WEB-INF/classes/configfiles/LogConfig.props");

		return bean;
	}
	/*
	@Bean
	public ServletRegistrationBean ReportScheduling() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ReportScheduling(), "/ReportScheduling");

	    return bean;
	}

	@Bean
	public ServletRegistrationBean ReportSchedulingDaily() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ReportSchedulingDaily(), "/ReportSchedulingDaily");


	    return bean;
	}

	@Bean
	public ServletRegistrationBean OperatorReceiver() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new OperatorReceiver(), "/OperatorReceiver");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("instanceCode","11");

	    return bean;
	}

	@Bean
	public ServletRegistrationBean Captcha() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ImageCaptcha(), "/Captcha.jpg");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("height","30");
	    params.put("width","120");

	    return bean;
	}

	@Bean
	public ServletRegistrationBean Report() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ReportServlet(), "/ReportServlet");

	    return bean;
	}*/


	/*@Bean
	public ServletRegistrationBean ComverseTestServer() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new ComverseTestServer(), "/ComverseTestServer");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("validationSleepTime","100");
	    params.put("topupSleepTime","10");
	    params.put("responseFilePath","/WEB-INF/classes/configfiles/INFiles/comverseResponse.props");
	    bean.setLoadOnStartup(3);
	    return bean;
	}
	*/

/*	@Bean
	public ServletRegistrationBean VASTestServer() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new VASTestServer(), "/VASTestServer");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("vasxmlfilepath","/WEB-INF/classes/configfiles/INFiles/VASRequestResponse.props");
	    bean.setLoadOnStartup(3);

	    return bean;
	}*/

	/*@Bean
	public ServletRegistrationBean SOSTestServer() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new SOSTestServer(), "/SOSTestServer");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("yabxxmlfilepath","/WEB-INF/classes/configfiles/INFiles/YABXRequestResponse.props");
	    bean.setLoadOnStartup(3);


	    return bean;
	}
	*/
	/*@Bean
	public ServletRegistrationBean CS5ClaroTestServer2() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new CS5ClaroTestServer2(), "/CS5ClaroTestServer2");

	    Map<String,String> params = new HashMap<String,String>();
	    params.put("cs5claroaxmlfilepath","/WEB-INF/classes/configfiles/INFiles/CS5ClaroRequestResponse1.props");
	    bean.setLoadOnStartup(5);


	    return bean;
	}*/



	/*@Bean
	public ServletRegistrationBean  action() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new ActionServlet(), "*.do");
		Map<String,String> params = new HashMap<String,String>();
		params.put("config","/WEB-INF/struts-config.xml," +
				"/WEB-INF/classes/configfiles/network/struts-config-network.xml," +
				"/WEB-INF/classes/configfiles/login/struts-config-login.xml," +
				"/WEB-INF/classes/configfiles/preference/struts-config-preference.xml," +
				"		/WEB-INF/classes/configfiles/configuration/struts-config-configuration.xml," +
				"		/WEB-INF/classes/configfiles/preference/struts-config-controlPreference.xml," +
				"		/WEB-INF/classes/configfiles/servicekeyword/struts-config-servicekeyword.xml," +
				"		/WEB-INF/classes/configfiles/user/struts-config-user.xml," +
				"		/WEB-INF/classes/configfiles/p2psubscriber/struts-config-p2psubscriber.xml," +
				"		/WEB-INF/classes/configfiles/gateway/struts-config-gateway.xml," +
				"		/WEB-INF/classes/configfiles/cardgroup/struts-config-cardgroup.xml," +
				"		/WEB-INF/classes/configfiles/interfaces/struts-config-interfaces.xml," +
				"		/WEB-INF/classes/configfiles/master/struts-config-master.xml," +
				"		/WEB-INF/classes/configfiles/subscriber/struts-config-subscriber.xml," +
				"		/WEB-INF/classes/configfiles/transferrules/struts-config-transferrules.xml," +
				"		/WEB-INF/classes/configfiles/iccidkeymgmt/struts-config-iccidkeymgmt.xml," +
				"		/WEB-INF/classes/configfiles/transfer/struts-config-transfer.xml," +
				"		/WEB-INF/classes/configfiles/voucherOrderRequest/struts-config-voucherReq.xml," +
				"		/WEB-INF/classes/configfiles/networkstock/struts-config-networkstock.xml," +
				"		/WEB-INF/classes/configfiles/profile/struts-config-profile.xml," +
				"		/WEB-INF/classes/configfiles/product/struts-config-product.xml," +
				"	        /WEB-INF/classes/configfiles/channeluser/struts-config-channeluser.xml," +
				"		/WEB-INF/classes/configfiles/reports/struts-config-reports.xml," +
				"		/WEB-INF/classes/configfiles/c2sreports/struts-config-c2sreports.xml,		" +
				"		/WEB-INF/classes/configfiles/userreturn/struts-config-userreturn.xml," +
				"                /WEB-INF/classes/configfiles/services/struts-config-services.xml," +
				"                /WEB-INF/classes/configfiles/transfer/struts-config-transfer.xml," +
				"		/WEB-INF/classes/configfiles/domain/struts-config-domain.xml," +
				"		/WEB-INF/classes/configfiles/cardgroup/struts-config-c2scardgroup.xml," +
				"		/WEB-INF/classes/configfiles/session/struts-config-session.xml," +
				"		/WEB-INF/classes/configfiles/p2pquery/struts-config-p2pquery.xml," +
				"		/WEB-INF/classes/configfiles/c2squery/struts-config-c2squery.xml," +
				"      	        /WEB-INF/classes/configfiles/roles/struts-config-userroles.xml," +
				"      	        /WEB-INF/classes/configfiles/routing/struts-config-routing.xml," +
				"      	        /WEB-INF/classes/configfiles/reconciliation/struts-config-reconciliation.xml," +
				"      	        /WEB-INF/classes/configfiles/whitelist/struts-config-whitelist.xml," +
				"		/WEB-INF/classes/configfiles/restrictedsubs/struts-config-restrictedsubs.xml," +
				"	        /WEB-INF/classes/configfiles/c2sreports/struts-config-restrictedsubscriber.xml," +
				"		/WEB-INF/classes/configfiles/batchfoc/struts-config-batchfoc.xml," +
				"		/WEB-INF/classes/configfiles/bulkuser/struts-config-bulkuser.xml," +
				"		/WEB-INF/classes/configfiles/c2sreports/struts-config-summary.xml," +
				"           /WEB-INF/classes/configfiles/vomscategory/struts-config-vomscategory.xml," +
				"           /WEB-INF/classes/configfiles/voucherbundle/struts-config-voucherbundle.xml," +
				"                /WEB-INF/classes/configfiles/vomsproduct/struts-config-vomsproduct.xml," +
				"                /WEB-INF/classes/configfiles/vomsreport/struts-config-vomsreport.xml," +
				"                /WEB-INF/classes/configfiles/vomsvoucher/struts-config-voucher.xml," +
				"		/WEB-INF/classes/configfiles/batchC2C/struts-config-batchc2c.xml," +
				"		/WEB-INF/classes/configfiles/viewedituser/struts-config-viewedituser.xml," +
				"		/WEB-INF/classes/configfiles/downloadreport/struts-config-downloadreports.xml," +
				"		/WEB-INF/classes/configfiles/alertmsisdn/struts-config-alertmsisdn.xml," +
				"		/WEB-INF/classes/configfiles/iatrestrictedsubs/struts-config-iatrestrictedsubs.xml," +
				"		/WEB-INF/classes/configfiles/c2sreports/struts-config-iatrestrictedsubsreport.xml," +
				"		/WEB-INF/classes/configfiles/privaterecharge/struts-config-privaterchrg.xml," +
				"		/WEB-INF/classes/configfiles/messages/struts-config-messages.xml," +
				"		/WEB-INF/classes/configfiles/batchO2C/struts-config-batcho2c.xml," +
				"        /WEB-INF/classes/configfiles/forcelmb/struts-config-forcelmb.xml," +
				"        /WEB-INF/classes/configfiles/cp2pbuddymgt/struts-config-cp2pbuddymgt.xml," +
				"		/WEB-INF/classes/configfiles/lms/struts-config-lms.xml," +
				"        /WEB-INF/classes/configfiles/loyalitystock/struts-config-loyalitystock.xml," +
				"         /WEB-INF/classes/configfiles/lmsreports/struts-config-lmsreports.xml," +
				"         /WEB-INF/classes/configfiles/adhoc/struts-config-adhoc.xml," +
				"	 /WEB-INF/classes/configfiles/autoO2C/struts-config-autoO2C.xml," +
				"	/WEB-INF/classes/configfiles/autoC2C/struts-config-autoC2C.xml," +
				"	/WEB-INF/classes/configfiles/iat/struts-config-iat.xml," +
				"	/WEB-INF/classes/configfiles/bonusbundle/struts-config-bonusmaster.xml," +
				"	/WEB-INF/classes/configfiles/cosmgmt/struts-config-cosmgmt.xml," +
				"	/WEB-INF/classes/configfiles/cellidmgmt/struts-config-cellidmgmt.xml," +
				"	/WEB-INF/classes/configfiles/servicegpmgmt/struts-config-servicegpmgmt.xml," +
				"		/WEB-INF/classes/configfiles/common/struts-config-common.xml," +
				"		/WEB-INF/classes/configfiles/iatreports/struts-config-iatreports.xml," +
				"		/WEB-INF/classes/configfiles/prodintermapping/struts-config-productintmapping.xml," +
				"		/WEB-INF/classes/configfiles/iatenquiry/struts-config-iatquery.xml"
		);



		params.put("debug","5");
		params.put("detail","2");
		params.put("ApplicationResourcesPath","/WEB-INF/classes/configfiles");
		// params.put("ApplicationResourcesPath","/WEB-INF/configfiles");
		params.put("MessageResourceName","MessageResources");
		bean.setInitParameters(params);
		bean.setLoadOnStartup(2);


		return bean;
	}
*/

/*	@Bean
	public ServletRegistrationBean mvcdispatcher() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(
	      new DispatcherServlet(), "*.form");

	 	    Map<String,String> params = new HashMap<String,String>();
	    params.put("contextConfigLocation","/WEB-INF/applicationContext.xml");
	    bean.setInitParameters(params);
	    bean.setLoadOnStartup(6);

	    return bean;
	}
*/








	@Bean
	public FilterRegistrationBean paramFilterBean() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(paramFilter());
		registration.addUrlPatterns("*.do");

		registration.addInitParameter("excludeParams", "(.*\\.|^|.*|\\[(&#39;|\"))(c|C)lass(\\.|(&#39;|\")]|\\[).*,^dojo\\..*,^struts\\..*,^session\\..*,^request\\..*,^application\\..*,^servlet(Request|Response)\\..*,^parameters\\..*,^action:.*,^method:.*");

		registration.setName("paramFilter");

		registration.setOrder(2);
		return registration;
	}

	public Filter paramFilter() {
		return new ParamFilter();
	}



	@Bean
	public FilterRegistrationBean paramWrapperFilterBean() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(paramWrapperFilter());
		registration.addUrlPatterns("*.do");

		registration.addInitParameter("excludeParams", "(.*\\.|^|.*|\\[('|\"))(c|C)lass(\\.|('|\")]|\\[).*");

		registration.setName("paramWrapperFilter");

		registration.setOrder(3);
		return registration;
	}

	public Filter paramWrapperFilter() {
		return new ParamWrapperFilter();
	}






	//@Bean
	public FilterRegistrationBean loadSaltBean() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(loadSalt());
		registration.addUrlPatterns("*.do");
		registration.addUrlPatterns("*.form");

		registration.setName("loadSalt");

		registration.setOrder(4);
		return registration;
	}

	public Filter loadSalt() {
		return new LoadSalt();
	}



	//@Bean
	public FilterRegistrationBean validateSaltBean() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(validateSalt());
		registration.addUrlPatterns("*.do");
		registration.addUrlPatterns("*.form");

		registration.setName("validateSalt");

		registration.setOrder(5);
		return registration;
	}

	public Filter validateSalt() {
		return new ValidateSalt();
	}



	@Bean
	public MessageSource messageSource() {

		ReloadableResourceBundleMessageSource messageSource  = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:configfiles/MessageResources", "classpath:configfiles/restservice", "classpath:configfiles/Messages", "classpath:configfiles/restapi", "classpath:configfiles/SpringMessages");
		return messageSource;
	}


	//@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		return builder.sources(PretupsStarter.class);
	}

	public static void shutdown() {
		Thread thread = new Thread(() -> {
			context.close();
		});

		thread.setDaemon(false);
		thread.start();
	}
	public static void main(String[] args) {
		SpringApplication sa = new SpringApplication(PretupsStarter.class);
		System.setProperty("pretups.tomcat.port", "9084");
		System.setProperty("pretups.instance.type", "WEB");   	// 	args[0]=WEB or SMS or REST
		System.setProperty("pretups.instance.module", "C2S");   //	args[1]=C2S or P2P or WEB or REST
		context = sa.run(args);

	}

	@RestController
	public static class WarInitializerController {

		@GetMapping("/test")
		public String handler() {
			return "Hello";
		}
	}

	    /*
	    @Bean
	    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
	      webServerFactoryCustomizer() {


	        return factory -> factory.setContextPath("/pretups");
	    }
	    */











	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {

		configurer.setUseTrailingSlashMatch(true);
		//configurer.setUseTrailingSlashMatch(false);

		configurer.addPathPrefix("/rstapi",
				HandlerTypePredicate.forAnnotation(RestController.class));
	}


@Bean
	CorsConfigurationSource corsConfigurationSource() {
		/*final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("*"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
*/

	String[] allowedOriginsArr= allowedOrigins.split(",");

	CorsConfiguration configuration = new CorsConfiguration();
	configuration.setAllowedOrigins(Arrays.asList(allowedOriginsArr));
	configuration.setAllowedMethods(Arrays.asList("GET","POST"));
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", configuration);


		return source;
	}




	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeHttpRequests()
				.anyRequest().permitAll();
		return http.build();



		/*http
				.authorizeRequests()
				.anyRequest()
				.permitAll().and()
				.csrf()
				.disable()
				.cors().configurationSource(corsConfigurationSource());


		return http.build();*/
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		converters.add(new StringHttpMessageConverter());
	}

	//@Bean
	public OpenAPI springShopOpenAPI() {

		Tag tag1 = new Tag();
		tag1.setName("Tag1");
		tag1.setDescription("Tag1 Desc");

		ArrayList<Tag> tagList = new ArrayList<>();
		tagList.add(tag1);

		return new OpenAPI()
				.info(new Info().title("PreTUPS")
						.description("PreTUPS APIs")
						.version("v7.64.0")
						.license(new License().name("PreTUPS License").url("http://springdoc.org")))
				.externalDocs(new ExternalDocumentation()
						.description("Documentation")
						.url("https://springshop.wiki.github.org/docs"))
				//.tags(tagList)
				;
	}



//	@Primary
//	@Bean
	public Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> apis(SwaggerUiConfigProperties swaggerUiConfig) {
//		URL url = Thread.currentThread().getContextClassLoader().getResource("swagger.json");


		Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrlSet = new HashSet<>();
		//externalDo.forEach(doc -> {
		//	String docName = doc.get("docName");
			AbstractSwaggerUiConfigProperties.SwaggerUrl wsResource = new AbstractSwaggerUiConfigProperties.SwaggerUrl("","http://localhost:9747/pretups/rstapi/v1/downloadFileTest/swagger.json","");
			swaggerUrlSet.add(wsResource);
		//});
		swaggerUiConfig.setUrls(swaggerUrlSet);
		return swaggerUrlSet;
	}

/*
	private ApiInfo metaData() {
		return new ApiInfoBuilder()
				.title("PreTUPS RoadMap")
				.description("\"PreTUPS RoapMap APIs\"")
				.version("")
				.license("")
				.licenseUrl(" \"")
				.contact(new Contact("", "", ""))
				.build();
	}



	private ApiKey apiKey() {
		return new ApiKey("Authorization", "Authorization", "header");
	}
	private List<Parameter> globalParamList(){

		List<Parameter>  list = new ArrayList<Parameter>();
		Parameter authTokenHeader =
				new ParameterBuilder()
						.name("NONCE") // name of the header
						.modelRef(new ModelRef("string")) // data-type of the header
						.required(true) // required/optional
						.parameterType("header") // for query-param, this value can be 'query'
						.description("NONCE")
						.build();

		Parameter authTokenHeader2 =
				new ParameterBuilder()
						.name("Signature") // name of the header
						.modelRef(new ModelRef("string")) // data-type of the header
						.required(true) // required/optional
						.parameterType("header") // for query-param, this value can be 'query'
						.description("Signature")
						.build();

		list.add(authTokenHeader);
		list.add(authTokenHeader2);

		return list;
	}



	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope
				= new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(
				new SecurityReference("AUTHORIZATION", authorizationScopes));
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex("/*"))
				.build();
	}*/

/*
	@Bean
	public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
		return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
				.forEach(operation -> operation.addParametersItem(new HeaderParameter().$ref("#/components/headers/Version")));
	}*/





	/*@Bean
	public OperationCustomizer customGlobalHeaders() {

		return (Operation operation, HandlerMethod handlerMethod) -> {

			Parameter missingParam1 = new Parameter()
					.in(ParameterIn.HEADER.toString())
					.schema(new StringSchema())
					.name("missingParam1")
					.description("header description2")
					.required(true);

			Parameter missingParam2 = new Parameter()
					.in(ParameterIn.HEADER.toString())
					.schema(new StringSchema())
					.name("missingParam2")
					.description("header description2")
					.required(true);

			operation.addParametersItem(missingParam1);
			operation.addParametersItem(missingParam2);

			return operation;
		};
	}
*/


	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
				.description(preTUPS_Desc)
				.title("PreTUPS").version("7.64.0"))




				// Components section defines Security Scheme "mySecretHeader"
				.components(new Components()
						.addSecuritySchemes("Authorization", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")))
				// AddSecurityItem section applies created scheme globally
				.addSecurityItem(new SecurityRequirement().addList("Authorization"));
	}

	//@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("File Operations")
				.pathsToMatch("/**")


			/*	.tags(new springfox.documentation.service.Tag("Authentication Management", "Authentication Management APIs"),
						new Tag("File Operations", "File Template and User list APIs"),
						new Tag("C2S Receiver", "C2S Receiver APIs"),
						new Tag("C2S Services", "C2S Services APIs"),
						new Tag("C2C Receiver", "C2C Receiver APIs") ,
						new Tag("Channel Users", "Channel Users APIs"),
						new Tag("User Services", "User Services APIs") ,
						new Tag("Voucher Services", "Voucher Services APIs"),
						new Tag("Regular Expression", "Regular Expressions"),
						new Tag("C2C Batch Services", "C2C Batch Services"))
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey())).apiInfo(metaData())
*/
				.build();
	}

/*

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any())

				.paths(PathSelectors.any()).build()
				.globalOperationParameters(globalParamList())
				.tags(new springfox.documentation.service.Tag("Authentication Management", "Authentication Management APIs"),
						new Tag("File Operations", "File Template and User list APIs"),
						new Tag("C2S Receiver", "C2S Receiver APIs"),
						new Tag("C2S Services", "C2S Services APIs"),
						new Tag("C2C Receiver", "C2C Receiver APIs") ,
						new Tag("Channel Users", "Channel Users APIs"),
						new Tag("User Services", "User Services APIs") ,
						new Tag("Voucher Services", "Voucher Services APIs"),
						new Tag("Regular Expression", "Regular Expressions"),
						new Tag("C2C Batch Services", "C2C Batch Services"))
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey())).apiInfo(metaData());


	}
*/



	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/jsp/**").addResourceLocations("/jsp/");
		registry.addResourceHandler("/jsp/common/**").addResourceLocations("/jsp/common/");


		registry.addResourceHandler("/jsp/common/datepicker/**").addResourceLocations("/jsp/common/datepicker/");
		registry.addResourceHandler("/jsp/**").addResourceLocations("/jsp/common/datepicker/css/");
		registry.addResourceHandler("/jsp/common/images/**").addResourceLocations("/jsp/common/images/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/core/**").addResourceLocations("/core/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/");
		registry.addResourceHandler("/swagger/**").addResourceLocations("/swagger/");
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");


		registry.addResourceHandler("/newMonitorServer/**").addResourceLocations("/newMonitorServer/");
		registry.addResourceHandler("/newMonitorServer/common/**").addResourceLocations("/newMonitorServer/common/");
		registry.addResourceHandler("/newMonitorServer/common/images/**").addResourceLocations("/newMonitorServer/common/images/");



	}

	//@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.setInitParameter("resteasy.servlet.mapping.prefix", "/rest");
	}

	/*@Override
	public void addInterceptors(final InterceptorRegistry registry) {

		// registry.addInterceptor(jwtInterceptor);
		//    registry.addInterceptor(customHandlerInterceptor);
		//     registry.addInterceptor(nonceInterceptor);

		registry.addInterceptor(new RestApiInterceptor()).addPathPatterns("/rstapi/v1/**");
	}*/


	@Bean
	public ServletRegistrationBean TestVomsReceiver() {
		ServletRegistrationBean bean = new ServletRegistrationBean(
				new VomsReciever(), "/VomsReciever");

		Map<String,String> params = new HashMap<String,String>();
		params.put("validationSleepTime","100");
		params.put("topupSleepTime","10");
		params.put("responseFilePath","/WEB-INF/classes/configfiles/INFiles/vomsResponse.props");
		bean.setLoadOnStartup(4);
		return bean;
	}

//		@Bean
//		public ServletRegistrationBean TestServer() {
//		    ServletRegistrationBean bean = new ServletRegistrationBean(
//		      new ComverseTestServer(), "/ComverseTestServer");
//		   
//		    Map<String,String> params = new HashMap<String,String>();
//		    params.put("validationSleepTime","100");
//		    params.put("topupSleepTime","10");
//		    params.put("responseFilePath","/WEB-INF/classes/configfiles/INFiles/comverseResponse.props");
//		    bean.setLoadOnStartup(3);
//		    return bean;
//		}



	/*@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(com.btsl.util.Constants.getProperty("AllowedMappings")).allowedOrigins(com.btsl.util.Constants.getProperty("AllowedOrigins")).allowedMethods("GET", "POST", "OPTIONS", "PUT","DELETE");
	}*/



	@Override
	public void addCorsMappings(CorsRegistry registry) {
	//	registry.addMapping("/**");

		String[] allowedOriginsArr= allowedOrigins.split(",");

		registry.addMapping("/**").
				allowedOrigins(allowedOriginsArr).
				allowedMethods("*").
				maxAge(-1) ;//.allowedMethods("GET", "POST", "OPTIONS", "PUT","DELETE");


	}




	@Bean("pretupsAsyncThreadExecutor")
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(corePoolSize);
		pool.setMaxPoolSize(maxPoolSize);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}

	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> registry.config().commonTags("application", "Pretups");
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub

		String[] beans = appContext.getBeanDefinitionNames();
		Arrays.sort(beans);
		for (String bean : beans)
		{
			_logger.debug("Spring Bean loading sequence",bean + " of Type :: " + appContext.getBean(bean).getClass());
		}

	}








	/**
	 * Method messageSource.
	 *
	 * @return ResourceBundleMessageSource
	 */
	@Bean
	public ResourceBundleMessageSource messageSource2() {
		ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		rs.setBasename("messages");
		rs.setDefaultEncoding("UTF-8");
		rs.setUseCodeAsDefaultMessage(true);
		return rs;
	}




	//@Bean
	CommandLineRunner runner() {
		return args -> {

			ExecutorService executor = Executors.newFixedThreadPool(NumberConstants.N10.getIntValue());
			vmsCacheRepoistory.loadLocaleMaster();
			executor.execute(new NetworkPreferenceCache());
			executor.execute(new SysPreferenceCache());
			executor.execute(new MessageGatewayRedisCache());
			executor.execute(new LookupCache());
			executor.execute(new MessageGatewayCache());


		};
	}

}