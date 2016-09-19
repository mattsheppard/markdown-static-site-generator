package com.kstruct.markdown.steps;

import com.kstruct.markdown.model.SiteModelNode;

public abstract class VisitorProcessor implements Processor {

    @Override
    public void process(SiteModelNode root) {
        visit(root);
        
        for (SiteModelNode child : root.getChildren()) {
            visit(child);
        }
    }

    public abstract void visit(SiteModelNode node);
    
}
