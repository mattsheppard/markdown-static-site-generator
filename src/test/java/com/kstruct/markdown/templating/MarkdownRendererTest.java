package com.kstruct.markdown.templating;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

public class MarkdownRendererTest {
	@Test
    public void testRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example heading");
		Assert.assertEquals("<h1 id=\"example-heading\">example heading</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testNonAsciiRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example 漏斗回");
		Assert.assertEquals("<h1 id=\"example-\">example 漏斗回</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testLinkFixing() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("[title](../foo/example.md)");
		
		Assert.assertEquals("<p><a href=\"../foo/example.html\">title</a></p>\n", result.getRenderedContent());

		// Link targets remain unchanged though
		Assert.assertEquals(1, result.getLinkTargets().size());
		Assert.assertTrue(result.getLinkTargets().contains("../foo/example.md"));
    }

    @Test
    public void testToc() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();
        MarkdownProcessorResult result = mr.process(
              "# a\n"
            + "## b\n"
            + "## c\n"
            + "### d\n"
            + "## e\n");
        
        TocTree expectedToc = new TocTree(null, new TocEntry("root", -1));
        
        TocTree a = new TocTree(expectedToc, new TocEntry("a", 1));
        TocTree b = new TocTree(a, new TocEntry("b", 2));
        TocTree c = new TocTree(a, new TocEntry("c", 2));
        TocTree d = new TocTree(c, new TocEntry("d", 3));
        TocTree e = new TocTree(a, new TocEntry("e", 2));
        
        c.getChildren().add(d);
        a.getChildren().add(b);
        a.getChildren().add(c);
        a.getChildren().add(e);

        expectedToc.getChildren().add(a);
        
        Assert.assertEquals(expectedToc, result.getToc());
    }
}
