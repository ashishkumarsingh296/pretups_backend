<#include "/${parameters.templateDir}/qxhtml/controlheader-core.ftl" />
<#if parameters.inputcolspan?exists>
	<#assign inputColumnSpan = parameters.inputcolspan />
<#else>
	<#assign inputColumnSpan = 1 />
</#if>
<#if (inputColumnSpan > 0)>
<td class="${parameters.cssClass?default('tabcol')?html}"<#rt/>
 colspan="${inputColumnSpan}">
<#if qTableLayout?exists && qTableLayout.tablecolspan?exists >
	<#assign columnCount = qTableLayout.currentColumnCount + inputColumnSpan />	
	<#-- update the value of the qTableLayout.currentColumnCount bean on the value stack. -->
	${stack.setValue('#qTableLayout.currentColumnCount', columnCount)}
</#if>
</#if>

