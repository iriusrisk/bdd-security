<#ftl strip_whitespace=true>
<html>
<head>
<title>${name}</title>
<style type="text/css" media="all">
@import url( "./style/jbehave-core.css" );
</style>
</head>
<body>
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
