<#ftl strip_whitespace=true>
<#macro renderMultiline text>${text?replace("\n", "<br/>")}</#macro>
<#macro renderMeta meta>
<meta>
<#assign metaProperties=meta.getProperties()>
<#list metaProperties.keySet() as name>
<#assign property = metaProperties.get(name)>
<property keyword="${keywords.metaProperty}" name="${name}" value="${property}"/>
</#list>
</meta>
</#macro>
<#macro renderNarrative narrative>
<narrative keyword="${keywords.narrative}">
<inOrderTo keyword="${keywords.inOrderTo}">${narrative.inOrderTo}</inOrderTo>
<asA keyword="${keywords.asA}">${narrative.asA}</asA>
<iWantTo keyword="${keywords.iWantTo}">${narrative.iWantTo}</iWantTo>
</narrative>
</#macro>
<#macro renderGivenStories givenStories>
<givenStories keyword"${keywords.givenStories}">
<#list givenStories.getStories() as givenStory>
<givenStory <#if givenStory.hasAnchor()>parameters="${givenStory.parameters}"</#if>>${givenStory.path}/>
</#list>
</givenStories>
</#macro>
<#macro renderTable table>
<#assign rows=table.getRows()>
<#assign headers=table.getHeaders()>
<table>
<headers>
<#list headers as header>
<header>${header}</header>
</#list>
</headers>
<#list rows as row>
<row>
<#list headers as header>
<#assign cell=row.get(header)>
<value>${cell}</value>
</#list>
</row>
</#list>
</table>
</#macro>
<#macro renderOutcomes table>
<#assign outcomes=table.getOutcomes()>
<#assign fields=table.getOutcomeFields()>
<outcomes>
<fields>
<#list fields as field>
<field>${field}</field>
</#list>
<fields>
<#list outcomes as outcome>
<#assign isVerified=outcome.isVerified()?string>
<#if isVerified == "true"> <#assign verified="verified"><#else><#assign verified="notVerified"></#if>
<outcome>
<value>${outcome.description}</value><value>${outcome.value}</value><value>${outcome.matcher}</value><value><#if isVerified == "true">${keywords.yes}<#else>${keywords.no}</#if></value>
</outcome>
</#list>
</outcomes>
</#macro>
<#macro renderStep step>
<#assign formattedStep = step.getFormattedStep("<parameter>{0}</parameter>")>
<step outcome="${step.outcome}">
${formattedStep} <#if step.getTable()??><parameter><@renderTable step.getTable()/></parameter></#if>
<#if step.getFailure()??><failure>${step.failureCause}</failure></#if><#if step.getOutcomes()??><@renderOutcomes step.getOutcomes()/></#if></step>
</#macro>

<story path="${story.path}" title="${story.description}">
<#if story.getMeta()??><@renderMeta story.getMeta()/></#if>
<#if story.getNarrative()??><@renderNarrative story.getNarrative()/></#if>
<#assign scenarios = story.getScenarios()>
<#list scenarios as scenario>
<scenario keyword="${keywords.scenario}" title="${scenario.title}">   
<#if scenario.getMeta()??><@renderMeta scenario.getMeta()/></#if>
<#if scenario.getGivenStories()??><@renderGivenStories scenario.getGivenStories()/></#if>
<#if scenario.getExamplesTable()??>
<examples keyword="${keywords.examplesTable}">
<#list scenario.getExamplesSteps() as step>
<step>${step?xml}</step>   
</#list>
<@renderTable scenario.getExamplesTable()/>
</examples>
<#if scenario.getExamples()??>
<#list scenario.getExamples() as example>
<example keyword="${keywords.examplesTableRow}">${example}</example>
<#assign steps = scenario.getStepsByExample(example)>
<#list steps as step>
<@renderStep step/>
</#list>
</#list>
</#if>
<#else> 
<#assign steps = scenario.getSteps()>
<#list steps as step>
<@renderStep step/>
</#list>
</#if>
</scenario> 
</#list>
<#if story.isCancelled()?string == 'true'>
<cancelled keyword="${keywords.storyCancelled}" durationKeyword="${keywords.duration}" durationInSecs="${story.storyDuration.durationInSecs}"/>
</#if>
</story>
<#if story.getPendingMethods()??>
<#list story.getPendingMethods() as method>
<pendingMethod>${method}</pendingMethod>
</#list>
</#if>

