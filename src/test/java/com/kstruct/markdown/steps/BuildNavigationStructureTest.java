package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.Directory;
import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.model.SiteModelNode;

public class BuildNavigationStructureTest {

    @Test
    public void testEmptyInputDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root/input");
        Files.createDirectories(root);
        
        SiteModelNode node = new BuildNavigationStructure(root).build();
        
        Assert.assertTrue(node instanceof Directory);
        Assert.assertEquals(0, node.getChildren().size());
    }

    @Test
    public void testMarkdownFiles() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);
        
        Path inputMd = input.resolve("example1.md");
        Files.write(inputMd, "# Example heading 1".getBytes(StandardCharsets.UTF_8));

        Path inputMd2 = input.resolve("example2.md");
        Files.write(inputMd2, "# Example heading 2".getBytes(StandardCharsets.UTF_8));

        SiteModelNode node = new BuildNavigationStructure(input).build();
        
        Assert.assertTrue(node instanceof Directory);
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertTrue(node.getChildren().get(0) instanceof MarkdownPage);
        Assert.assertTrue(node.getChildren().get(1) instanceof MarkdownPage);
    }

    @Test
    public void testNonUrlSafeFileNames() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);
        
        Path inputMd = input.resolve("example number one.md");
        Files.write(inputMd, "# Spaces example".getBytes(StandardCharsets.UTF_8));

        Path inputMd2 = input.resolve("example漏斗回.md");
        Files.write(inputMd2, "# Chinese example".getBytes(StandardCharsets.UTF_8));

        SiteModelNode node = new BuildNavigationStructure(input).build();
        // Primarily we're checking that nothing blows up
        
        Assert.assertTrue(node instanceof Directory);
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertTrue(node.getChildren().get(0) instanceof MarkdownPage);
        Assert.assertTrue(node.getChildren().get(1) instanceof MarkdownPage);
    }

    @Test
    public void testPageSorting() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);
        
        Path inputMd = input.resolve("example2.md");
        Files.write(inputMd, "# Example".getBytes(StandardCharsets.UTF_8));

        Path inputMd2 = input.resolve("example1.md");
        Files.write(inputMd2, "# Example".getBytes(StandardCharsets.UTF_8));

        SiteModelNode node = new BuildNavigationStructure(input).build();
        // Primarily we're checking that nothing blows up
        
        Assert.assertTrue(node instanceof Directory);
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertEquals("example1.md", node.getChildren().get(0).getRelativeSourcePath().getFileName().toString());
        Assert.assertEquals("example2.md", node.getChildren().get(1).getRelativeSourcePath().getFileName().toString());
    }

}
