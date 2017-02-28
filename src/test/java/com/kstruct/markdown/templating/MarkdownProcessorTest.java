package com.kstruct.markdown.templating;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.commonmark.node.Visitor;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

public class MarkdownProcessorTest {
	@Test
    public void testRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example heading", Arrays.asList(new Visitor[]{}));
		Assert.assertEquals("<h1 id=\"example-heading\">example heading</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testNonAsciiRendering() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("# example 漏斗回", Arrays.asList(new Visitor[]{}));
		Assert.assertEquals("<h1 id=\"example-\">example 漏斗回</h1>\n", result.getRenderedContent());
    }

	@Test
    public void testLinkFixing() throws IOException {
		MarkdownProcessor mr = new MarkdownProcessor();
		MarkdownProcessorResult result = mr.process("[title](../foo/example.md)", Arrays.asList(new Visitor[]{}));
		
		Assert.assertEquals("<p><a href=\"../foo/example.html\">title</a></p>\n", result.getRenderedContent());

		// Link targets remain unchanged though
		Assert.assertEquals(1, result.getLinkTargets().size());
		Assert.assertTrue(result.getLinkTargets().contains("../foo/example.md"));
    }

	   @Test
	    public void testLinkFragment() throws IOException {
	        MarkdownProcessor mr = new MarkdownProcessor();
	        MarkdownProcessorResult result = mr.process("[title](example.md#someheading)", Arrays.asList(new Visitor[]{}));
	        
	        Assert.assertEquals("<p><a href=\"example.html#someheading\">title</a></p>\n", result.getRenderedContent());
	    }

    @Test
    public void testToc() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();
        MarkdownProcessorResult result = mr.process(
              "# a\n"
            + "## b\n"
            + "## c\n"
            + "### d\n"
            + "## e\n"
            , Arrays.asList(new Visitor[]{}));
        
        TocTree expectedToc = new TocTree(null, new TocEntry("root", -1, ""));
        
        TocTree a = new TocTree(expectedToc, new TocEntry("a", 1, "a"));
        TocTree b = new TocTree(a, new TocEntry("b", 2, "b"));
        TocTree c = new TocTree(a, new TocEntry("c", 2, "c"));
        TocTree d = new TocTree(c, new TocEntry("d", 3, "d"));
        TocTree e = new TocTree(a, new TocEntry("e", 2, "e"));
        
        c.getChildren().add(d);
        a.getChildren().add(b);
        a.getChildren().add(c);
        a.getChildren().add(e);

        expectedToc.getChildren().add(a);
        
        Assert.assertEquals(expectedToc, result.getToc());
    }
    
    @Test
    public void testHeadingExpansion() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();
        MarkdownProcessorResult result = mr.process("# Generated Section - Pages\n## Generated Section - Categories\n", 
        		Arrays.asList(new Visitor[]{
        				new NavigationLinkInjector(
        				    Paths.get("/example/example.md"), Paths.get("/example"),
    						Arrays.asList(new String[]{"foo.md", "bar.md"}), Arrays.asList(new String[]{"cat1", "cat2"})
					)
        			}
			)
    		);
        Assert.assertEquals("<h1 id=\"pages\">Pages</h1>\n"
            + "<ul>\n"
            + "<li><a href=\"foo.html\">Foo</a></li>\n"
            + "<li><a href=\"bar.html\">Bar</a></li>\n"
            + "</ul>\n"
            + "<h2 id=\"categories\">Categories</h2>\n"
            + "<ul>\n"
            + "<li><a href=\"cat1/index.html\">Cat1</a></li>\n"
            + "<li><a href=\"cat2/index.html\">Cat2</a></li>\n"
            + "</ul>\n", result.getRenderedContent());
    }

    @Test
    public void testHeadingRemovalEmptyList() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();

        MarkdownProcessorResult result = mr.process("Something\n# Generated Section - Pages\n## Generated Section - Categories\n", 
        		Arrays.asList(new Visitor[]{
        				new NavigationLinkInjector(
                            Paths.get("/example/example.md"), Paths.get("/example"),
    						Arrays.asList(new String[]{}), Arrays.asList(new String[]{})
					)
        			}
			)
    		);
        Assert.assertEquals("<p>Something</p>\n", result.getRenderedContent());
    }

    @Test
    public void testTableRendering() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();
        MarkdownProcessorResult result = mr.process("| Tables        | Are           | Cool  |\r\n| ------------- |:-------------:| -----:|\r\n| one      | two | three |\r\n", Arrays.asList(new Visitor[]{}));
        Assert.assertEquals("<table>\n<thead>\n<tr><th>Tables</th><th align=\"center\">Are</th><th align=\"right\">Cool</th></tr>\n</thead>\n<tbody>\n<tr><td>one</td><td align=\"center\">two</td><td align=\"right\">three</td></tr>\n</tbody>\n</table>\n", result.getRenderedContent());
    }

    @Test
    public void testYamlFrontMatter() throws IOException {
        MarkdownProcessor mr = new MarkdownProcessor();
        MarkdownProcessorResult result = mr.process("---\nkey: value\n---\ncontent", Arrays.asList(new Visitor[]{}));
        Assert.assertEquals("<p>content</p>\n", result.getRenderedContent());
        Assert.assertEquals(ImmutableMap.of("key", ImmutableList.of("value")), result.getMetadata());
    }

}
