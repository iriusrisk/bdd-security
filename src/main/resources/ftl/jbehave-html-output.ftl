<#ftl strip_whitespace=true>
<#macro renderMultiline text>${text?replace("\n", "<br/>")}</#macro>
<#macro renderMeta meta>
<div class="meta">
<div class="keyword">${keywords.meta}</div>
<#assign metaProperties=meta.getProperties()>
<#list metaProperties.keySet() as name>
<#assign property = metaProperties.get(name)>
<div class="property">${keywords.metaProperty}${name} ${property}</div>
</#list>
</div>
</#macro>
<#macro renderNarrative narrative>
<div class="narrative"><h2>${keywords.narrative}</h2>
<div class="element inOrderTo"><span class="keyword inOrderTo">${keywords.inOrderTo}</span> ${narrative.inOrderTo}</div>
<div class="element asA"><span class="keyword asA">${keywords.asA}</span> ${narrative.asA}</div>
<div class="element iWantTo"><span class="keyword iWantTo">${keywords.iWantTo}</span> ${narrative.iWantTo}</div>
</div>
</#macro>
<#macro renderGivenStories givenStories>
<div class="givenStories">
<div class="keyword">${keywords.givenStories}</div>
<#list givenStories.getStories() as givenStory>
<div class="givenStory">${givenStory.path}</div>
</#list>
</div>
</#macro>
<#macro renderTable table>
<#assign rows=table.getRows()>
<#assign headers=table.getHeaders()>
<table>
<thead><tr>
<#list headers as header>
<th>${header}</th>
</#list>
</tr></thead>
<tbody>
<#list rows as row>
<tr>
<#list headers as header>
<#assign cell=row.get(header)>
<td>${cell}</td>
</#list>
</tr>
</#list>
</tbody>
</table>
</#macro>
<#macro renderOutcomes table>
<#assign outcomes=table.getOutcomes()>
<#assign fields=table.getOutcomeFields()>
<table>
<thead><tr>
<#list fields as field>
<th>${field}</th>
</#list>
</tr></thead>
<tbody>
<#list outcomes as outcome>
<#assign isVerified=outcome.isVerified()?string>
<#if isVerified == "true"> <#assign verified="verified"><#else><#assign verified="notVerified"></#if>
<tr class="${verified}">
<td>${outcome.description}</td><td>${outcome.value}</td><td>${outcome.matcher}</td><td><#if isVerified == "true">${keywords.yes}<#else>${keywords.no}</#if></td>
</tr>
</#list>
</tbody>
</table>
</#macro>
<#macro renderStep step>
<#assign formattedStep = step.getFormattedStep("<span class=\"step parameter\">{0}</span>")>
<div class="step ${step.outcome}">${formattedStep} <#if step.getTable()??><span class="step parameter"><@renderTable step.getTable()/></span></#if> <@renderStepOutcome step.getOutcome()/></div>
<#if step.getFailure()??><pre class="failure">${step.failureCause}</pre></#if>
<#if step.getOutcomes()??>
<div class="outcomes"><@renderOutcomes step.getOutcomes()/>
<#if step.getOutcomesFailureCause()??><pre class="failure">${step.outcomesFailureCause}</pre></#if>
</div>
</#if>
</#macro>
<#macro renderStepOutcome outcome><#if outcome=="pending"><span class=\"keyword ${outcome}\">(${keywords.pending})</span></#if><#if outcome=="failed"><span class=\"keyword ${outcome}\">(${keywords.failed})</span></#if><#if outcome=="notPerformed"><span class=\"keyword ${outcome}\">(${keywords.notPerformed})</span></#if></#macro>

<html>
<body>
<div class="story"><h1><@renderMultiline story.getDescription()/></h1>
<div class="path">${story.path}</div>
<#if story.getMeta()??><@renderMeta story.getMeta()/></#if>
<#if story.getNarrative()??><@renderNarrative story.getNarrative()/></#if>
<#assign scenarios = story.getScenarios()>
<#list scenarios as scenario>
<div class="scenario"><h2>${keywords.scenario} <@renderMultiline scenario.getTitle()/></h2>   
<#if scenario.getMeta()??><@renderMeta scenario.getMeta()/></#if>
<#if scenario.getGivenStories()??><@renderGivenStories scenario.getGivenStories()/></#if>
<#if scenario.getExamplesTable()??>
<div class="examples"><h3>${keywords.examplesTable}</h3>
<#list scenario.getExamplesSteps() as step>
<div class="step">${step?html}</div>   
</#list>
<@renderTable scenario.getExamplesTable()/>
</div>  <!-- end examples -->
<#if scenario.getExamples()??>
<#list scenario.getExamples() as example>
<h3 class="example">${keywords.examplesTableRow} ${example}</h3>
<#assign steps = scenario.getStepsByExample(example)>
<#list steps as step>
<@renderStep step/>
</#list>
</#list>
</#if>
<#else> <!-- normal scenario steps -->
<#assign steps = scenario.getSteps()>
<#list steps as step>
<@renderStep step/>
</#list>
</#if>
</div> <!-- end scenario -->
</#list>
<#if story.isCancelled()?string == 'true'>
<div class="cancelled">${keywords.storyCancelled} (${keywords.duration} ${story.storyDuration.durationInSecs} s)</div>
</#if>
</div> <!-- end story -->
<#if story.getPendingMethods()??>
<#list story.getPendingMethods() as method>
<div><pre class="pending">${method}</pre></div>
</#list>
</#if>
</body>
</html>

