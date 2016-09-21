<html>
    <head>
        <title>${title} - ${siteName} - Version ${extraConfig.version}</title>
    </head>
     <body>
         <h1>${title}</h1>
        ${content}

        <ul>
            <@render_navigation node=navigationRoot/>
        </ul>
     </body>
</html>

 <#macro render_navigation node>
    <li><a href="${relativeRootUri}${node.outputPath}">${node.title}</a>
        <#list node.children as child>
            <#if node.hasHtmlPagesBelow>
                <ul>
                    <@render_navigation node=child/>
                </ul>
            </#if>
        </#list>
    </li>
</#macro>