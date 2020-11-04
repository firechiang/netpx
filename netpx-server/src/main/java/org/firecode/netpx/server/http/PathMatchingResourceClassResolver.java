package org.firecode.netpx.server.http;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.firecode.netpx.common.util.ClassUtil;
import org.firecode.netpx.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ChiangFire
 */
public class PathMatchingResourceClassResolver {

	private static final Logger LOG = LoggerFactory.getLogger(PathMatchingResourceClassResolver.class);

	private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	private static final String CLASSP_RESOURCE_SUFFIX = ".class";

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*" + CLASSP_RESOURCE_SUFFIX;

	public static final char PACKAGE_SEPARATOR = '.';

	public static final char PATH_SEPARATOR = '/';

	public static final String DEFAULT_PATH_SEPARATOR = "/";

	public Set<Class<?>> doFindMatchingFileClasses(String basePackage) {
		String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + basePackage.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR) + PATH_SEPARATOR + DEFAULT_RESOURCE_PATTERN;
		String rootDirPath = determineRootDir(packageSearchPath);
		String subPattern = packageSearchPath.substring(rootDirPath.length());
		String path = rootDirPath.substring(CLASSPATH_ALL_URL_PREFIX.length());
		Set<Class<?>> result = new LinkedHashSet<>();
		ClassLoader cl = ClassUtil.getDefaultClassLoader();
		try {
			Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
			while (resourceUrls.hasMoreElements()) {
				URL url = resourceUrls.nextElement();
				try {
					File rootDir = new File(new URI(StringUtil.replace(url.toString(), " ", "%20")).getSchemeSpecificPart());
					Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
					for (File file : matchingFiles) {
						try {
							String replace = file.getPath().replace(File.separatorChar, PACKAGE_SEPARATOR);
							String className = replace.substring(replace.indexOf(basePackage), replace.length() - 6);
							Class<?> clazz = Class.forName(className);
							result.add(clazz);
						} catch (ClassNotFoundException e) {
							LOG.warn("The exception was not handled", e);
						}
					}
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private String determineRootDir(String location) {
		int prefixEnd = location.indexOf(':') + 1;
		int rootDirEnd = location.length();
		while (rootDirEnd > prefixEnd && StringUtil.isPattern(location.substring(prefixEnd, rootDirEnd))) {
			rootDirEnd = location.lastIndexOf(PATH_SEPARATOR, rootDirEnd - 2) + 1;
		}
		if (rootDirEnd == 0) {
			rootDirEnd = prefixEnd;
		}
		return location.substring(0, rootDirEnd);
	}

	private Set<File> retrieveMatchingFiles(File rootDir, String pattern) {
		if (!rootDir.exists()) {
			return Collections.emptySet();
		}
		if (!rootDir.isDirectory()) {
			return Collections.emptySet();
		}
		if (!rootDir.canRead()) {
			return Collections.emptySet();
		}
		String fullPattern = StringUtil.replace(rootDir.getAbsolutePath(), File.separator, DEFAULT_PATH_SEPARATOR);
		if (!pattern.startsWith(DEFAULT_PATH_SEPARATOR)) {
			fullPattern += DEFAULT_PATH_SEPARATOR;
		}
		fullPattern = fullPattern + StringUtil.replace(pattern, File.separator, DEFAULT_PATH_SEPARATOR);
		Set<File> result = new LinkedHashSet<>();
		doRetrieveMatchingFiles(fullPattern, rootDir, result);
		return result;
	}

	private void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) {
		File[] dirContents = dir.listFiles();
		if (dirContents != null) {
			Arrays.sort(dirContents);
			for (File content : dirContents) {
				if (content.isDirectory() && content.canRead()) {
					doRetrieveMatchingFiles(fullPattern, content, result);
				}
				if (content.getPath().endsWith(CLASSP_RESOURCE_SUFFIX)) {
					result.add(content);
				}
			}
		}
	}
}
