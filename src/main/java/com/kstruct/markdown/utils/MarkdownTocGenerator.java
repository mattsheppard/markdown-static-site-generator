package com.kstruct.markdown.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

public class MarkdownTocGenerator implements AttributeProvider {
    private List<TocEntry> toc = new ArrayList<>();

    public TocTree getToc() {
        // We turn the list into a tree for the convenience of the template
        TocTree root = new TocTree(null, new TocEntry("root", -1));
        
        TocTree currentNode = root;
        
        for (TocEntry t : toc) {
            // Move currentNode up until it's at t's parent
            while (t.getLevel() <= currentNode.getDetails().getLevel()) {
                currentNode = currentNode.getParent();
            }
            
            // Add t as a child of currentNode
            TocTree newNode = new TocTree(currentNode, t);
            currentNode.getChildren().add(newNode);

            // t's newNode becomes the new currentNode
            currentNode = newNode;
        }
        
        return root;
    }
    
    @Override
    public void setAttributes(Node node, Map<String, String> attributes) {
        if (node instanceof Heading) {
            MarkdownTextVisitor textVisitor = new MarkdownTextVisitor();
            node.accept(textVisitor);
            
            String label = textVisitor.getText();
            Integer level = ((Heading) node).getLevel();
            
            TocEntry tocEntry = new TocEntry(label, level);
            toc.add(tocEntry);
            
            attributes.put("id", tocEntry.getAnchorId());
        }
    }
}
