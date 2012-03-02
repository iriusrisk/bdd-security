<#ftl strip_whitespace=true>
<#macro renderStat stats name description class=""><#assign value = stats.get(name)!"N/A"><#if (value != "0")><span class="${class}">${description} ${value}</span><#else>${description} ${value}</#if></#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>JBehave Views</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<style type="text/css" media="all">
@import url( "./style/jbehave-core.css" );
</style>
</head>

<body>
<div id="banner"><img src="images/jbehave-logo.png" alt="jbehave" />
<div class="clear"></div>
</div>

<div class="views">

<h2>Story Views</h2>

<table>
<thead>
<tr><th>Name</th><th>Description</th><th>View</th></tr>
</thead>
<tbody>
<tr><td>Story Maps</td>
    <td>A view on the story maps generated for the configured meta filters and stories</td>
    <td><a href="maps.html">html</a></td>
</tr>
<tr>
    <td>Story Reports</td>
    <td>A view on the story reports generated when running the stories</td>
    <td><a href="reports.html">html</a></td>
</tr>
</tbody>
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
