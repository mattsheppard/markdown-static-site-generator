## 0.0.7-SNAPSHOT

## 0.0.6
- Issue #15 - Ensure heading IDs are not duplicated
- Issue #12 - Added default values for parameters matching maven standard layout as follows
-- inputDirectory = ${project.basedir}/src/main/resources/markdown
-- outputDirectory = ${project.build.directory}/generated-site
-- template = ${project.basedir}/src/main/resources/template.ftl
-- strictLinkChecking = true

## 0.0.5
- Issue #9 : Use metadata with key title in preference, even in the navigation structure where it was not previosuly available .
- Issue #13 : Fail the maven build if the processing of one of the pages fails.

## 0.0.4
- Added support for YAML Frontmatter to specify metadata or titles
- Changed template to have default HTML escaping (means you'll need to ?no_esc the markdown body in your template if you do nothing any take the default)

## 0.0.3
- First released version
