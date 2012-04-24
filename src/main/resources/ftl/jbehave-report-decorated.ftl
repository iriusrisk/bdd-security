<#ftl strip_whitespace=true>
<html>
<head>
<title>${name}</title>
<style type="text/css" media="all">
@import url( "./style/jbehave-core.css" );
</style>
</head>
<body>
<div id="banner"><img src="images/jbehave-logo.png" alt="jbehave" />
<div class="clear"></div>
<#if format == "html">
${body}
<#else>
<#assign brushFormat = format> <#if format == "stats"><#assign brushFormat = "plain"> </#if>
<script type="syntaxhighlighter" class="brush: ${brushFormat}"><![CDATA[
${body}
]]></script>
</#if>
</body>
<#include "./sh.ftl">
</html>
