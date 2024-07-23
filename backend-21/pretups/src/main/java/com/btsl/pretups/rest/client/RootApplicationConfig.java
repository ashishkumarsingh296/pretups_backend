package com.btsl.pretups.rest.client;


import com.annotation.RestEasyAnnotation;
import com.btsl.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.function.Predicate;

@Configuration
@EnableWebMvc
public class RootApplicationConfig implements WebMvcConfigurer {

	@Value("${ALLOWED_ORIGINS}")
	String allowedOrigins;

/*
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseTrailingSlashMatch(true);
	}*/

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {

		configurer.setUseTrailingSlashMatch(true);


		Predicate<Class<?>> rstapipredicate = clazz ->
				clazz.isAnnotationPresent(RestController.class) &&
						!clazz.isAnnotationPresent(RestEasyAnnotation.class);


		Predicate<Class<?>> resteaypredicate = clazz ->
				clazz.isAnnotationPresent(RestController.class) &&
						clazz.isAnnotationPresent(RestEasyAnnotation.class);

		configurer.addPathPrefix("/rest", resteaypredicate);
		configurer.addPathPrefix("/rstapi", rstapipredicate);
	}



	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		String[] allowedOriginsArr= allowedOrigins.split(",");
		registry.addMapping("/**") .
				allowedOrigins(allowedOriginsArr) .
				allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") .
				allowedHeaders("*") .maxAge(-1) ;//.allowCredentials(true);
	}
}

	//Read more: https://www.java67.com/2023/07/how-to-configure-cors-in-spring-boot.html#ixzz8JP2hs8LO


		/*
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.*;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.LookupCache;
import com.btsl.user.businesslogic.MessageGatewayCache;
import com.btsl.user.businesslogic.MessageGatewayRedisCache;
import com.btsl.user.businesslogic.NetworkPreferenceCache;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.SysPreferenceCache;
import com.btsl.user.businesslogic.VMSCacheRepository;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.google.common.collect.Lists;


import lombok.val;
*/
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
*//*


*/
/**
 *
 * @author akhilesh.mittal1
 * Custom Configurator for Swagger and Spring REST Support
 *//*

*/
/*

@Configuration
@EnableWebMvc
@EnableSwagger2
@EnableAsync
//@EnableJpaRepositories(basePackages = {"com.btsl.user.businesslogic", "com.restapi.oauth.services"} )
//@ComponentScan(basePackages = {"com.restapi.simulator",  "com.btsl.common","com.restapi", "com.restapi.user.service", "com.btsl.pretups.channel.transfer.requesthandler","com.btsl.pretups.channel.transfer","com.restapi.cardgroup.service","com.restapi.networkstock.service" , "com.btsl.user.businesslogic",  "com.restapi.oauth.services"})
@EntityScan(basePackages = {"com.btsl.user.businesslogic", "com.restapi.oauth.services", "com.btsl.user.businesslogic.entity"})
@EnableJpaRepositories(basePackages = {"com.btsl.user.businesslogic", "com.restapi.oauth.services"} , entityManagerFactoryRef="entityManagerFactory")
*//*


public class RootApplicationConfig */
/*extends WebMvcConfigurationSupport*//*
 */
/*implements WebMvcConfigurer*//*
 {

	private static Log _logger = LogFactory.getLog(RootApplicationConfig.class.getName());



	@Autowired
	private ApplicationContext appContext;

	private static Environment environment;
	*/
/*
	@Value("${threadpool.corepoolsize}")
    int corePoolSize;

    @Value("${threadpool.maxpoolsize}")
    int maxPoolSize;*//*



	//  @Autowired
	//  JwtInterceptor jwtInterceptor;

	  */
/*  @Autowired
	    CustomHandlerInterceptor customHandlerInterceptor;

	    @Autowired
	    private NonceInterceptor nonceInterceptor;
*//*


	@Autowired
	private VMSCacheRepository vmsCacheRepoistory;


	//@Override
	public void addInterceptors(InterceptorRegistry registry){
		// registry.addInterceptor(jwtInterceptor);
		//   registry.addInterceptor(customHandlerInterceptor);
		//   registry.addInterceptor(nonceInterceptor);
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		//  properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");

		return properties;
	}


	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		try {

			String constantspropsfile = null;
			String serverHomePath = null;
			serverHomePath = System.getProperty("catalina.base");
			if (System.getProperty("os.name").contains("Windows")) {
				constantspropsfile = serverHomePath + "\\conf\\pretups\\Constants.props";
			} else {
				constantspropsfile = serverHomePath + "/conf/pretups/Constants.props";
			}
			try {
				Constants.load(constantspropsfile);
			}catch(Exception e){
				_logger.error("dataSource", e.getMessage());
			}

			dataSource.setDriverClassName(com.btsl.util.Constants.getProperty("driverClassName"));
			dataSource.setUrl(com.btsl.util.Constants.getProperty("datasourceurl"));
			dataSource.setUsername(com.btsl.util.Constants.getProperty("ROOT_CONFIG_USER_ID"));
			dataSource.setPassword(com.btsl.util.Constants.getProperty("ROOT_CONFIG_PASSWD"));
		}catch(Exception e) {
			e.printStackTrace();
		}

		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em
				= new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "com.btsl.user.businesslogic.entity", "com.btsl.user.businesslogic", "com.restapi.oauth.services" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	//@Override
	public void /data1(PathMatchConfigurer configurer) {
		configurer.setUseTrailingSlashMatch(false);
		configurer.addPathPrefix("/rstapi",
				HandlerTypePredicate.forAnnotation(RestController.class));
	}

	//@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(com.btsl.util.Constants.getProperty("AllowedMappings")).allowedOrigins(com.btsl.util.Constants.getProperty("AllowedOrigins")).allowedMethods("GET", "POST", "OPTIONS", "PUT","DELETE");
	}

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
	} */
/*
	private ApiKey apiKeyNonce() {
        return new ApiKey("NONCE", "NONCE", "header");
    }
	private ApiKey apiKeySignature() {
        return new ApiKey("Signature", "Signature", "header");
    }
	*//*



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


	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any())

				.paths(PathSelectors.any()).build()
				.globalOperationParameters(globalParamList())
				.tags(
						new Tag("Authentication Management", "Authentication Management APIs"),
						new Tag("C2C File Operations", "C2C File APIs"),
						new Tag("C2S Receiver", "C2S Receiver APIs"),
						new Tag("C2S Services", "C2S Services APIs"),
						new Tag("C2C Receiver", "C2C Receiver APIs") ,
						new Tag("Channel Users", "Channel Users APIs"),
						new Tag("User Services", "User Services APIs") ,
						new Tag("Voucher Services", "Voucher Services APIs"),
						new Tag("Regular Expression", "Regular Expressions"))
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey())).apiInfo(metaData())
				.genericModelSubstitutes(Optional.class);


	}



	//@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/jsp/**").addResourceLocations("/jsp/");
		registry.addResourceHandler("/jsp/common/**").addResourceLocations("/jsp/common/");
		registry.addResourceHandler("/common/**").addResourceLocations("/common/");
		registry.addResourceHandler("/common/images/**").addResourceLocations("/common/images/");
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



	}


	@Bean("pretupsAsyncThreadExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(10);
		pool.setMaxPoolSize(50);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}






	*/
/**
	 * Method messageSource.
	 *
	 * @return ResourceBundleMessageSource
	 *//*

	@Bean
	public ResourceBundleMessageSource messageSource2() {
		ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		rs.setBasename("messages");
		rs.setDefaultEncoding("UTF-8");
		rs.setUseCodeAsDefaultMessage(true);
		return rs;
	}




	@Bean
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
*/
