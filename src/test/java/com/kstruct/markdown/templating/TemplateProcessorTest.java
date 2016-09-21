package com.kstruct.markdown.templating;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TemplateProcessorTest {
	@Test
    public void testTemplateProcessor() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root");
        Files.createDirectories(root);
        
        String template = "<html>${siteName} ${extraConfig.extraConfigExample} ${content}</html>";
        
        Path ftl = root.resolve("example.ftl");
        Files.write(ftl, template.getBytes(StandardCharsets.UTF_8));

		String siteName = "Site Name";
		Map<String, Object> extraConfig = new HashMap<>();
		extraConfig.put("extraConfigExample", "extraConfigExampleValue");
		
		TemplateProcessor tp = new TemplateProcessor(ftl, null, siteName, extraConfig);
		String result = tp.template("example 漏斗回");
		Assert.assertEquals("<html>Site Name extraConfigExampleValue example 漏斗回</html>", result);
    }
}
