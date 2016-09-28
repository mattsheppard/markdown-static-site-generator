package com.kstruct.markdown.templating;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

public class TemplateProcessorTest {
	@Test
    public void testTemplateProcessor() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root");
        Files.createDirectories(root);
        
        String template = "<html>${siteName} ${title} ${toc.details.label} ${relativeUri} ${relativeRootUri} ${extraConfig.extraConfigExample} ${content}</html>";
        
        Path ftl = root.resolve("example.ftl");
        Files.write(ftl, template.getBytes(StandardCharsets.UTF_8));

		String siteName = "Site Name";
		Map<String, Object> extraConfig = new HashMap<>();
		extraConfig.put("extraConfigExample", "extraConfigExampleValue");
		
		TocTree toc = new TocTree(null, new TocEntry("label", 1));
		
		TemplateProcessor tp = new TemplateProcessor(ftl, null, siteName, extraConfig);
		String result = tp.template("example 漏斗回", "title", toc, "relativeUri", "relativeRootUri");
		Assert.assertEquals("<html>Site Name title label relativeUri relativeRootUri extraConfigExampleValue example 漏斗回</html>", result);
    }
}
