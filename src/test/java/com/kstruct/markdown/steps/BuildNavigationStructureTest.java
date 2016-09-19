package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.navigation.NavigationNode;

public class BuildNavigationStructureTest {

    @Test
    public void testEmptyInputDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/input");
        Files.createDirectory(root);
        
        NavigationNode node = new BuildNavigationStructure(root).build();
        
        Assert.assertNotNull(node);
    }
    
}
