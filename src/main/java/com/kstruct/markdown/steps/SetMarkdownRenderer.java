package com.kstruct.markdown.steps;

import java.nio.file.Path;
import java.util.Map;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.model.SiteModelNode;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;

public class SetMarkdownRenderer extends VisitorProcessor {
    
    private MarkdownRenderer markdownRenderer;

    public SetMarkdownRenderer() {
        markdownRenderer = new MarkdownRenderer();
    }

    @Override
    public void visit(SiteModelNode node) {
        if (node instanceof MarkdownPage) {
            // Only the markdown pages know about templates
            MarkdownPage page = (MarkdownPage) node;
            
            page.setMarkdownRenderer(this.markdownRenderer);
        }
    }


}
