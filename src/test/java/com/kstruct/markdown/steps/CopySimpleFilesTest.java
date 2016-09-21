package com.kstruct.markdown.steps;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class CopySimpleFilesTest {
    @Test
    public void testCopyingFiles() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path exampleCss = input.resolve("example.css");
        Files.write(exampleCss, "css".getBytes(StandardCharsets.UTF_8));

        Path exampleImage = input.resolve("subdir/image.jpg");
        Files.createDirectories(exampleImage.getParent());
        Files.write(exampleImage, "jpg".getBytes(StandardCharsets.UTF_8));
        
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ExecutorService pool = mock(ExecutorService.class);
        // This is not ideal - We depend on the internal detail of calling execute on the pool
        // where as there are many other possible pool methods which could be used instead.
        Mockito.doNothing().when(pool).execute(runnableCaptor.capture());
        
        CopySimpleFiles csf = new CopySimpleFiles();
        csf.queueCopyOperations(input, output, pool);

        // Should not exist yet - Should be created by the Runnable, not during the queue call
        Assert.assertFalse(Files.exists(output.resolve("example.css")));
        Assert.assertFalse(Files.exists(output.resolve("subdir/image.jpg")));
        
        List<Runnable> runnables = runnableCaptor.getAllValues();
        Assert.assertEquals(2, runnables.size());
        runnables.forEach(r -> r.run());
        
        Assert.assertTrue(Files.exists(output.resolve("example.css")));
        Assert.assertTrue(Files.exists(output.resolve("subdir/image.jpg")));

        Assert.assertEquals("css", new String(Files.readAllBytes(output.resolve("example.css")), StandardCharsets.UTF_8));
        Assert.assertEquals("jpg", new String(Files.readAllBytes(output.resolve("subdir/image.jpg")), StandardCharsets.UTF_8));
    }
}
