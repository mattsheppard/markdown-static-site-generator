package com.kstruct.markdown.templating;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class MarkdownRendererTest {
	@Test
    public void testRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example heading");
		Assert.assertEquals("<h1>example heading</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testNonAsciiRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example 漏斗回");
		Assert.assertEquals("<h1>example 漏斗回</h1>\n", result.getRenderedContent());
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

}
