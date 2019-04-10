package com.kstruct.markdown.utils;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class MarkdownUtilsTest {
    @Test
    public void isMarkdownIndexPage() {
        Assert.assertTrue(MarkdownUtils.isMarkdownIndexPage(Paths.get("index.md")));
        Assert.assertFalse(MarkdownUtils.isMarkdownIndexPage(Paths.get("fooindex.md")));
    }
}