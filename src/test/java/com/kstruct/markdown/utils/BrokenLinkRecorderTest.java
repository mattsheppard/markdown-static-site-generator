package com.kstruct.markdown.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class BrokenLinkRecorderTest {

    @Test
    public void testLinkToHtmlDoc() throws IOException{
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());

        Files.createDirectories(fs.getPath("foo", "bar"));
        Assert.assertTrue(Files.exists(fs.getPath("foo", "bar")));
        Files.write(fs.getPath("foo", "bar", "plop.html"), "any old content".getBytes(StandardCharsets.UTF_8));

        Assert.assertTrue(BrokenLinkRecorder.linkExists(fs.getPath("foo", "bar", "plop.md")));

        Assert.assertFalse(BrokenLinkRecorder.linkExists(fs.getPath("foo", "bar", "plop.jpg")));
    }

}