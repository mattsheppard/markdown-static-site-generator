package com.kstruct.markdown.templating;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.commonmark.html.HtmlRenderer;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.node.Visitor;
import org.commonmark.parser.Parser;

import com.kstruct.markdown.utils.MarkdownUtils;

public class MarkdownProcessor {

	private Parser parser;
	private HtmlRenderer renderer;

	public MarkdownProcessor() {
		parser = Parser.builder().build();
		renderer = HtmlRenderer.builder().build();
	}

	public MarkdownProcessorResult process(String markdownContent) {
		Node document = parser.parse(markdownContent);
		
		Set<String> linkTargets = new HashSet<>();
		
		// Fix links to *.md to go to *.html instead
		document.accept(new AbstractVisitor() {
			@Override
			public void visit(Link link) {
			    URI uri = URI.create(link.getDestination());
				linkTargets.add(link.getDestination());
				link.setDestination(MarkdownUtils.renameFilenameForMarkdownPage(link.getDestination()));
				visitChildren(link);
			}
		});

		String renderedContent = renderer.render(document);

		MarkdownProcessorResult result = new MarkdownProcessorResult(renderedContent);
		result.getLinkTargets().addAll(linkTargets);
		return result;
	}

}
