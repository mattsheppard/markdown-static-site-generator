# markdown-static-site-generator

A simple maven plugin that takes a directory of markdown files (and resource files) and produces a static website. We expect commonmark style markdown syntax, and use the freemarker templating language to wrap the generated pages.

## So, how do I use it?

So, it's not in maven cerntral for now (will have to find out how to do that sometime), so you'll first need to clone it, then `mvn install` it locally.

Once you've got it available, you install it as a maven plugin on a project with something like this:

```
<project>
...
    <build>
...
        <plugins>
...
            <plugin>
                <groupId>com.kstruct</groupId>
                <artifactId>markdown-static-site-generator-maven-plugin</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <executions>
                    <execution>
                        <id>generate-static-site</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>generate-static-site</goal>
                        </goals>
                        <configuration>
                            <inputDirectory>${project.basedir}/docs</inputDirectory>
                            <outputDirectory>${project.basedir}/site</outputDirectory>
                            <template>${project.basedir}/example-real-template.ftl</template>
                            <siteName>Your Site Name</siteName>
                            <strictLinkChecking>true</strictLinkChecking>
                            <extraConfig>
                                <version>1.2.3</version>
                            </extraConfig>
                         </configuration>
                     </execution>
                 </executions>
             </plugin>
...
        </plugins>
...
    </build>
...
<project>
```

## Why not JBake, mkdocs, something else?

Most of the something-elses seem to focus on blogs rather than simple websites. Mkdocs worked fine for a while, but got slow with a hundreds of pages, and doesn't provide a a good hierarchical page representation to the template (for building a complete navigation menu). I looked briefly at JBake as an alternative, but was first annoyed by the compulsory metadata block, and then never got any reply on https://groups.google.com/forum/#!topic/jbake-user/nuDCRvAeGk4.

No doubt someone will now point out the perfect existing system for what I want, or why building my own will be harder than I think (https://twitter.com/pinboard/status/761656824202276864) - Oh well.
