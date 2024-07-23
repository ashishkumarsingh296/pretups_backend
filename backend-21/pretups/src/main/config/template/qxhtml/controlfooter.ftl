${parameters.after?if_exists}<#t/>
<#--
	Only show message if errors are available.
	This will be done if ActionSupport is used.
-->
<#assign hasFieldErrors = parameters.name?exists && fieldErrors?exists && fieldErrors[parameters.name]?exists/>
<#if hasFieldErrors>
<br />
<#list fieldErrors[parameters.name] as error>
<span errorFor="${parameters.id}" class="errorMessage">${error?html}</span><#t/>
</#list>
</#if>
<#if parameters.inputcolspan?exists>
	<#assign inputColumnSpan = parameters.inputcolspan />
<#else>
	<#assign inputColumnSpan = 1 />
</#if>
<#if (inputColumnSpan > 0)>
</td><#lt/><#-- Write out the closing td for the html input -->
</#if>
<#include "/${parameters.templateDir}/qxhtml/controlfooter-trlogic.ftl" />
