<#include "/${parameters.templateDir}/qxhtml/form-validate.ftl" />
<#include "/${parameters.templateDir}/simple/form.ftl" />
<table cellpadding="3" cellspacing="1" width="80%" class="${parameters.cssClass?default('wwFormTableC')?html}"<#rt/>
<#if parameters.cssStyle?exists> style="${parameters.cssStyle?html}"<#rt/>
</#if>
>
<input type="hidden" name = "tablecolspan" value="${qTableLayout.tablecolspan}"/>