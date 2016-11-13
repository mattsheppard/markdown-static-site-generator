package com.kstruct.markdown.testing.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import lombok.Data;
import lombok.SneakyThrows;

public class MockFilesystemUtils {

	/**
	 * Will give you a quick test filesystem with the following:
	 * 
	 * - /root/input directory
	 * - /root/output directory
	 * - Each given file created with the given bytes (and any parent dirs created)
	 * -- If relative paths are given, they're resolved relative to /root/input
	 * @throws IOException 
	 */
	public static FileSystem createMockFileSystem(FileWithContent[] files) throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        for (FileWithContent file : files) {
        		Path p = input.resolve(file.getPath());
        		Files.createDirectories(p.getParent());
        		Files.write(p, file.getContent());
        }
        
        return fs;
	}
	
	@Data
	public static class FileWithContent {
		private final String path;
		private final byte[] content;
	}
}
