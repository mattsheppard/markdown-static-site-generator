package com.kstruct.markdown.freemarker.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.TemplateLoader;
import lombok.Synchronized;

/**
 * Simple template loader which uses nio, since Freemarker doesn't seem to have
 * one.
 * 
 * Can be used in conjunction with jifs for unit tests in memory.
 */
public class NioTemplateLoader implements TemplateLoader {

    private Path root;
    private Map<Path, Reader> openReaders = new HashMap<>();

    /**
     * Create a new NioTemplateLoader to load Freemarker templates.
     * 
     * Can be used in conjunction with jifs for unit tests in memory, as well as
     * if you just want to use Paths rather than Files.
     * 
     * @param root
     *            A path to the root from which templates should be loaded.
     */
    public NioTemplateLoader(Path root) {
        this.root = root;
    }

    @Override
    public Path findTemplateSource(String name) throws IOException {
        Path result = root.resolve(name);
        if (Files.exists(result)) {
            return result;
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        Path source = (Path) templateSource;

        try {
            return Files.getLastModifiedTime(source).toMillis();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Synchronized // Simplify interacting with openReaders
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Path source = (Path) templateSource;

        Reader reader = Files.newBufferedReader(source, Charset.forName(encoding));
        openReaders.put(source, reader);
        return reader;
    }

    @Override
    @Synchronized // Simplify interacting with openReaders
    public void closeTemplateSource(Object templateSource) throws IOException {
        Path source = (Path) templateSource;

        if (openReaders.containsKey(source)) {
            openReaders.get(source).close();
            openReaders.remove(source);
        }
    }

}
