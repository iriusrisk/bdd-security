<#ftl strip_whitespace=true>
<#macro renderStat stats name description class=""><#assign value = stats.get(name)!"N/A"><#if (value != "0")><span class="${class}">${description} ${value}</span><#else>${description} ${value}</#if></#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>JBehave Maps</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<style type="text/css" media="all">
@import url( "./style/jbehave-core.css" );
</style>
</head>

<body>
<div id="banner"><img src="images/jbehave-logo.png" alt="jbehave" />
<div class="clear"></div>
</div>

<div class="maps">

<h2>Story Maps</h2>

<#assign lanes = storyLanes.getLanes()>
<#assign stories = storyLanes.getStories()>

<table>
<tr><th>Story Name</th>
<#list lanes as lane>
<th class="lane">${lane}</th>
</#list>
</tr>
<#list stories as story>
<tr>
<td class="name">${story.name}</td>
<#list lanes as lane>
<td class="lane">
<#assign inLane = storyLanes.inLane(lane, story)>
${inLane?string("x", "")}
</td>
</#list>
</tr>
</#list>
</table>
<br />
</div>

<div class="clear"></div>
<div id="footer">
<div class="left">Generated on ${date?string("dd/MM/yyyy HH:mm:ss")}</div>
<div class="right">JBehave &#169; 2003-2011</div>
<div class="clear"></div>
</div>

</body>
</html>
