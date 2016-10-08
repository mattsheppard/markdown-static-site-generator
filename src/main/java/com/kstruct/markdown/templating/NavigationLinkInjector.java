package com.kstruct.markdown.templating;

import java.nio.file.Paths;
import java.util.List;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Text;

import com.kstruct.markdown.utils.MarkdownTextVisitor;
import com.kstruct.markdown.utils.MarkdownUtils;
import com.kstruct.markdown.utils.PathUtils;

public class NavigationLinkInjector extends AbstractVisitor {
    private List<String> siblingPages;
	private List<String> subCategories;

	public NavigationLinkInjector(List<String> siblingPages, List<String> subCategories) {
    		this.siblingPages = siblingPages;
    		this.subCategories = subCategories;
	}

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
                    Link link = new Link(siblingPage, null);
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
                    Link link = new Link(subCategory + "/" + MarkdownUtils.DIRECTORY_INDEX_FILE_NAME + MarkdownUtils.MARKDOWN_FILE_EXTENSION, null);
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

}
