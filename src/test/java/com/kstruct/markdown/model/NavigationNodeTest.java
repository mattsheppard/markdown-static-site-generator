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

        MarkdownPage child = new MarkdownPage(root.resolve("example.md"), root, Optional.of(directory));
        directory.getChildren().add(child);

        Assert.assertTrue(directory.getHasHtmlPagesBelow());
        Assert.assertFalse(directory.isLeafHtmlPage());

        Assert.assertFalse(child.getHasHtmlPagesBelow());
        Assert.assertTrue(child.isLeafHtmlPage());
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

    @Test
    public void testPageDetectionForRoot() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
        
        NavigationNode n = new Directory(root.resolve(""), root, Optional.empty());
        
        Assert.assertTrue(n.isParentOfPageAt("foo/bar/example.html"));
        Assert.assertTrue(n.isParentOfPageAt("f/bar/example.html"));
        Assert.assertTrue(n.isParentOfPageAt("bar/example.html"));
        Assert.assertTrue(n.isParentOfPageAt("foo/bar/index.html"));
    }

    @Test
    public void testFindNodeFor() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
        
        NavigationNode topLevel = new Directory(root.resolve(""), root, Optional.empty());

        NavigationNode secondLevelA = new Directory(root.resolve("a"), root, Optional.empty());
        NavigationNode secondLevelB = new Directory(root.resolve("b"), root, Optional.empty());

        topLevel.getChildren().add(secondLevelA);
        topLevel.getChildren().add(secondLevelB);

        MarkdownPage child = new MarkdownPage(root.resolve("b/example.md"), root, Optional.of(secondLevelB));
        secondLevelB.getChildren().add(child);

        Assert.assertEquals(child, topLevel.findNodeFor("b/example.html").get());
    }

}
