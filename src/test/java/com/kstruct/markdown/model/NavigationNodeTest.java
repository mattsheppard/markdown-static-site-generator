package com.kstruct.markdown.model;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class NavigationNodeTest {
	@Test
	public void testGetHasHtmlPagesBelow() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
		
		Directory directory = new Directory(root, root, Optional.empty());
		
		Assert.assertFalse(directory.getHasHtmlPagesBelow());
		
		directory.getChildren().add(new MarkdownPage(root.resolve("example.md"), root, Optional.of(directory)));

		Assert.assertTrue(directory.getHasHtmlPagesBelow());
	}
	
	// TODO - Add tests for node.isPageAt(relativeUri) and node.isParentOfPageAt(relativeUri)
}
