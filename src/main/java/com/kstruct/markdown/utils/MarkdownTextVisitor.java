package com.kstruct.markdown.utils;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Text;

/**
 * Accumulates all text below the node and provides access to it after the fact
 */
public class MarkdownTextVisitor extends AbstractVisitor{
    private final StringBuilder sb = new StringBuilder();

    public String getText() {
        return sb.toString();
    }

    @Override
    public void visit(Text text) {
        sb.append(text.getLiteral());
    }
}
