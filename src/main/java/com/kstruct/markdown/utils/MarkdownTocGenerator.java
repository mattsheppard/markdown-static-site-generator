package com.kstruct.markdown.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.commonmark.html.AttributeProvider;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import com.kstruct.markdown.model.TocEntry;

import lombok.Data;
import lombok.Getter;

public class MarkdownTocGenerator implements AttributeProvider {
    @Getter
    private List<TocEntry> toc = new ArrayList<>();

    private int nextIndex = 1;
    
    @Override
    public void setAttributes(Node node, Map<String, String> attributes) {
        if (node instanceof Heading) {
            MarkdownTextVisitor textVisitor = new MarkdownTextVisitor();
            node.accept(textVisitor);
            
            String label = textVisitor.getText();
            Integer index = nextIndex;
            Integer level = ((Heading) node).getLevel();
            
            toc.add(new TocEntry(label, index, level));
            
            attributes.put("name", index.toString());
            
            nextIndex++;
        }
    }
}
