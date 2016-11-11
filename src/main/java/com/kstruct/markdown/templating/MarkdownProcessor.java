package com.kstruct.markdown.templating;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.gfm.tables.TablesExtension;
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
import org.commonmark.node.ListBlock;
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
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import com.kstruct.markdown.utils.MarkdownTextVisitor;
import com.kstruct.markdown.utils.MarkdownTocGenerator;
import com.kstruct.markdown.utils.MarkdownUtils;
import com.kstruct.markdown.utils.PathUtils;

public class MarkdownProcessor {

    public MarkdownProcessorResult process(String markdownContent, List<String> siblingPages, List<String> subCategories) {
        List<Extension> extensions = Arrays.asList(TablesExtension.create(), YamlFrontMatterExtension.create());

        Parser parser = Parser.builder().extensions(extensions).build();
        
        Node document = parser.parse(markdownContent);
        
        YamlFrontMatterVisitor yamlVisitor = new YamlFrontMatterVisitor();
        document.accept(yamlVisitor);
        
        Set<String> linkTargets = new HashSet<>();

        // Modify listing headings
        document.accept(new NavigationLinkInjector(siblingPages, subCategories));
        
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
        
        MarkdownTocGenerator tocGenerator = new MarkdownTocGenerator();

        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).attributeProviderFactory(new AttributeProviderFactory() {
            @Override
            public AttributeProvider create(AttributeProviderContext context) {
                return tocGenerator;
            }
        }).build();

        String renderedContent = renderer.render(document);

        MarkdownProcessorResult result = new MarkdownProcessorResult(renderedContent, tocGenerator.getToc(), yamlVisitor.getData());
        result.getLinkTargets().addAll(linkTargets);
        return result;
    }

}
