<table width="100%">
<tr>
    <td colspan="2"><div <#rt/>
<#if parameters.align?exists>
    align="${parameters.align?html}"<#t/>
</#if>
><#t/>
<#include "/${parameters.templateDir}/simple/submit.ftl" />
</div><#t/>
<#include "/${parameters.templateDir}/${parameters.theme}/controlfooter.ftl" />
