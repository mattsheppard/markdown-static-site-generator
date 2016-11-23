package com.kstruct.markdown.templating;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Assert;
import org.junit.Test;

public class NavigationLinkInjectorTest {

	@Test
	public void test() {
        String testInputDocument = "Foo\n"
        		+ "# Generated Section - Pages\n"
        		+ "Bar\n"
        		+ "## Generated Section - Categories\n";

        String testOutputDocument = "Foo\n"
        		+ "# Pages\n"
        		+ "* [Foo](foo.md)\n"
        		+ "* [Bar](bar.md)\n"
        		+ "\nBar\n"
        		+ "## Categories\n"
        		+ "* [Goo](goo/index.md)\n"
        		+ "* [Gar](gar/index.md)\n";

        Node testDocument = Parser.builder().build().parse(testInputDocument);

        Node expectedDocument = Parser.builder().build().parse(testOutputDocument);

        List<String> siblingPages = Arrays.asList(new String[]{"foo.md", "bar.md"});
        List<String> subCategories = Arrays.asList(new String[]{"goo", "gar"});
        
        NavigationLinkInjector navigationLinkInjector = new NavigationLinkInjector(
            Paths.get("/example/example.md"), Paths.get("/example"),
            siblingPages, subCategories);

        testDocument.accept(navigationLinkInjector);
        
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        
        Assert.assertEquals(renderer.render(expectedDocument), renderer.render(testDocument));
	}
	
}
