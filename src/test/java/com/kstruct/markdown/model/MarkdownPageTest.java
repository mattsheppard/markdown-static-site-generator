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

public class MarkdownPageTest {
	@Test
	public void testTitle() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
		
		MarkdownPage mdp = new MarkdownPage(root.resolve("example-to-process.md"), root, Optional.empty());
		
		Assert.assertEquals("Example To Process", mdp.getTitle());
	}
}
