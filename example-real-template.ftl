<html>
    <head>
        <title>${title} - ${siteName} - Version ${extraConfig.version}</title>
    </head>
    <body>
        <#if toc.children?size &gt; 0>
            <ul>
                <#list toc.children as node>
                    <@render_toc node=node/>
                </#list>
            </ul>
        </#if>

         <h1>${title}</h1>
        ${content}

        <ul>
            <@render_navigation node=navigationRoot/>
        </ul>
    </body>
</html>

 <#macro render_toc node>
    <li class="size${node.details.level}">
        <a href="#${node.details.anchorId}">${node.details.label}</a>
        <#list node.children as child>
                <ul>
                    <@render_toc node=child/>
                </ul>
        </#list>
    </li>
</#macro>

 <#macro render_navigation node>
    <li class="<#if node.isPageAt(relativeUri)>selected<#elseif node.isParentOfPageAt(relativeUri)>open</#if>">
    	<a href="${relativeRootUri}${node.outputPath}">${node.title}</a>
        <#list node.children as child>
            <#if child.hasHtmlPagesBelow>
                <ul>
                    <@render_navigation node=child/>
                </ul>
            </#if>
        </#list>
    </li>
</#macro>
