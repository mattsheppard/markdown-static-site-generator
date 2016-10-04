package com.kstruct.markdown.templating;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonmark.html.AttributeProvider;
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

import com.kstruct.markdown.utils.MarkdownTextVisitor;
import com.kstruct.markdown.utils.MarkdownTocGenerator;
import com.kstruct.markdown.utils.MarkdownUtils;
import com.kstruct.markdown.utils.PathUtils;

public class MarkdownProcessor {

    private Parser parser;

    public MarkdownProcessor() {
        parser = Parser.builder().build();
    }

    public MarkdownProcessorResult process(String markdownContent, List<String> siblingPages, List<String> subCategories) {
        Node document = parser.parse(markdownContent);

        Set<String> linkTargets = new HashSet<>();

        // Modify listing headings
        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Heading heading) {
                MarkdownTextVisitor textVisitor = new MarkdownTextVisitor();
                heading.accept(textVisitor);
                String headingName = textVisitor.getText();
                                
                // We do some special processing with certain heading names
                if (headingName.equals("Generated Section - Pages")) {
                    if (siblingPages.isEmpty()) {
                        heading.unlink();
                    } else {
                        Heading newHeading = new Heading();
                        newHeading.setLevel(heading.getLevel());
                        newHeading.appendChild(new Text("Pages"));
                        heading.insertAfter(newHeading);
                        heading.unlink(); // Remove the old heading
                        
                        BulletList list = new BulletList();
                        for (String siblingPage : siblingPages) {
                            ListItem item = new ListItem();
                            String title = PathUtils.titleForPath(Paths.get(siblingPage));
                            Link link = new Link(siblingPage, title);
                            link.appendChild(new Text(title));
                            item.appendChild(link);
                            list.appendChild(item);
                        }
                        newHeading.insertAfter(list);
                    }
                }

                if (headingName.equals("Generated Section - Categories")) {
                    if (subCategories.isEmpty()) {
                        heading.unlink();
                    } else {
                        Heading newHeading = new Heading();
                        newHeading.setLevel(heading.getLevel());
                        newHeading.appendChild(new Text("Categories"));
                        heading.insertAfter(newHeading);
                        heading.unlink(); // Remove the old heading
    
                        BulletList list = new BulletList();
                        for (String subCategory : subCategories) {
                            ListItem item = new ListItem();
                            String title = PathUtils.titleForPath(Paths.get(subCategory));
                            Link link = new Link(subCategory + "/" + MarkdownUtils.DIRECTORY_INDEX_FILE_NAME + MarkdownUtils.MARKDOWN_FILE_EXTENSION,
                                title);
                            item.appendChild(link);
                            link.appendChild(new Text(title));
                            item.appendChild(link);
                            list.appendChild(item);
                        }
                        newHeading.insertAfter(list);
                    }
                }
                
                visitChildren(heading);
            }
        });
        
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
        HtmlRenderer renderer = HtmlRenderer.builder().attributeProvider(tocGenerator).build();

        String renderedContent = renderer.render(document);

        MarkdownProcessorResult result = new MarkdownProcessorResult(renderedContent, tocGenerator.getToc());
        result.getLinkTargets().addAll(linkTargets);
        return result;
    }

}
