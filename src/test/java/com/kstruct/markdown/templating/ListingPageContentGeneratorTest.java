package com.kstruct.markdown.templating;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.templating.ListingPageContentGenerator.ListingPageContent;

public class ListingPageContentGeneratorTest {

    @Test
    public void testListingPageContentGenerator() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path one = input.resolve("foo/bar/goo/gar/index.md");
        Files.createDirectories(one.getParent());
        Files.write(one, "foo".getBytes(StandardCharsets.UTF_8));

        Path two = input.resolve("foo/bar/goo/gar/b.md");
        Files.createDirectories(two.getParent());
        Files.write(two, "foo".getBytes(StandardCharsets.UTF_8));

        Path three = input.resolve("foo/bar/goo/gar/c/c.md");
        Files.createDirectories(three.getParent());
        Files.write(three, "foo".getBytes(StandardCharsets.UTF_8));

        Path four = input.resolve("foo/bar/goo/gar/d/d.md");
        Files.createDirectories(four.getParent());
        Files.write(four, "foo".getBytes(StandardCharsets.UTF_8));

        Path five = input.resolve("foo/bar/goo/gar/.gitignore");
        Files.createDirectories(five.getParent());
        Files.write(five, "foo".getBytes(StandardCharsets.UTF_8));

        ListingPageContent result = new ListingPageContentGenerator().getListingPageContent(one);
        
        Assert.assertEquals(Arrays.asList(new String[]{"b.md"}), result.getPages());
        Assert.assertEquals(Arrays.asList(new String[]{"c", "d"}), result.getCategories());
    }
}
