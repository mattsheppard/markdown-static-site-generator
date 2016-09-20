package com.kstruct.markdown.templating;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class MarkdownRendererTest {
	@Test
    public void testRendering() throws IOException {
		MarkdownRenderer mr = new MarkdownRenderer();
		String result = mr.render("# example heading");
		Assert.assertEquals("<h1>example heading</h1>\n", result);
    }

	@Test
    public void testNonAsciiRendering() throws IOException {
		MarkdownRenderer mr = new MarkdownRenderer();
		String result = mr.render("# example 漏斗回");
		Assert.assertEquals("<h1>example 漏斗回</h1>\n", result);
    }
}
