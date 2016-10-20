package com.kstruct.markdown.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class PathUtilsTest {

    @Test
    public void testSimplePage() {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");

        Path markdown = input.resolve("example.md");
        
        String title = PathUtils.titleForPath(markdown,  input);
        
        Assert.assertEquals("Example", title);
    }
    
    @Test
    public void testRootPage() {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");

        Path markdown = input.resolve("index.md");
        
        String title = PathUtils.titleForPath(markdown,  input);
        
        Assert.assertEquals("", title);
    }
}
