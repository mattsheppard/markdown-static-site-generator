package com.kstruct.markdown.templating;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.kstruct.markdown.model.TocEntry;

public class MarkdownRendererTest {
	@Test
    public void testRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example heading");
		Assert.assertEquals("<h1 name=\"1\">example heading</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testNonAsciiRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example 漏斗回");
		Assert.assertEquals("<h1 name=\"1\">example 漏斗回</h1>\n", result.getRenderedContent());
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
        MarkdownProcessorResult result = mr.process("# a\n## b\n## c\n### d\n## e\n");
        
        List<TocEntry> expectedToc = new ArrayList<>();
        expectedToc.add(new TocEntry("a", 1, 1));
        expectedToc.add(new TocEntry("b", 2, 2));
        expectedToc.add(new TocEntry("c", 3, 2));
        expectedToc.add(new TocEntry("d", 4, 3));
        expectedToc.add(new TocEntry("e", 5, 2));
        
        Assert.assertEquals(expectedToc, result.getToc());
    }
}
