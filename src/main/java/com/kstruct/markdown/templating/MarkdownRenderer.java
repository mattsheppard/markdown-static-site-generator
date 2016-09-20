package com.kstruct.markdown.templating;

import org.commonmark.html.HtmlRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

public class MarkdownRenderer {

	private Parser parser;
	private HtmlRenderer renderer;

    public MarkdownRenderer() {
		parser = Parser.builder().build();
		renderer = HtmlRenderer.builder().build();
    }

	public String render(String markdownContent) {
        Node document = parser.parse(markdownContent);
        String renderedContent = renderer.render(document);
		
        return renderedContent;
	}
    
}
