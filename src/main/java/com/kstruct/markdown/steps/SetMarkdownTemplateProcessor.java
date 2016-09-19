package com.kstruct.markdown.steps;

import java.nio.file.Path;
import java.util.Map;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.model.SiteModelNode;
import com.kstruct.markdown.templating.TemplateProcessor;

public class SetMarkdownTemplateProcessor extends VisitorProcessor {
    
    private TemplateProcessor templateProcessor;

    public SetMarkdownTemplateProcessor(Path template, String siteName, Map<String, Object> extraConfig) {
        templateProcessor = new TemplateProcessor(template, siteName, extraConfig);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void visit(SiteModelNode node) {
        if (node instanceof MarkdownPage) {
            // Only the markdown pages know about templates
            MarkdownPage page = (MarkdownPage) node;
            
            page.setTemplateProcessor(this.templateProcessor);
        }
    }


}
