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

    @Test
    public void testPageDetectionForPage() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
        
        NavigationNode n = new MarkdownPage(root.resolve("foo/bar/example.md"), root, Optional.empty());
        
        Assert.assertTrue(n.isPageAt("foo/bar/example.html"));
        Assert.assertFalse(n.isPageAt("f/bar/example.html"));
        Assert.assertFalse(n.isPageAt("bar/example.html"));
        Assert.assertFalse(n.isPageAt("foo/bar/index.html"));
    }

    @Test
    public void testPageDetectionForDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
        
        NavigationNode n = new Directory(root.resolve("foo/bar"), root, Optional.empty());
        
        Assert.assertTrue(n.isParentOfPageAt("foo/bar/example.html"));
        Assert.assertFalse(n.isParentOfPageAt("f/bar/example.html"));
        Assert.assertFalse(n.isParentOfPageAt("bar/example.html"));
        Assert.assertTrue(n.isParentOfPageAt("foo/bar/index.html"));
    }

}
