package co.ntier.web.pipeline.impl.closure;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.SneakyThrows;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.SourceFile;

/**
 * Adapted from Closure's CommandLineRunner to provide externs
 * 
 * See https://github.com/adinardi/google-closure-compiler/blob/master/src/com/google/javascript/jscomp/CommandLineRunner.java
 */
class ClosureHelper {
	
	// The externs expected in externs.zip, in sorted order.
		private static final List<String> DEFAULT_EXTERNS_NAMES = ImmutableList.of(
				// JS externs
				"es3.js",
				"es5.js",

				// Event APIs
				"w3c_event.js",
				"w3c_event3.js",
				"gecko_event.js",
				"ie_event.js",
				"webkit_event.js",

				// DOM apis
				"w3c_dom1.js",
				"w3c_dom2.js",
				"w3c_dom3.js",
				"gecko_dom.js",
				"ie_dom.js",
				"webkit_dom.js",

				// CSS apis
				"w3c_css.js",
				"gecko_css.js",
				"ie_css.js",
				"webkit_css.js",

				// Top-level namespaces
				"google.js",

				"deprecated.js", "fileapi.js", "flash.js", "gears_symbols.js",
				"gears_types.js", "gecko_xml.js", "html5.js", "ie_vml.js",
				"iphone.js", "webstorage.js", "w3c_anim_timing.js", "w3c_css3d.js",
				"w3c_elementtraversal.js", "w3c_geolocation.js",
				"w3c_indexeddb.js", "w3c_navigation_timing.js", "w3c_range.js",
				"w3c_selectors.js", "w3c_xml.js", "window.js",
				"webkit_notifications.js", "webgl.js");

	/**
	 * @return a mutable list
	 * @throws IOException
	 */
	@SneakyThrows
	public static List<SourceFile> getDefaultExterns(){
		InputStream input = CommandLineRunner.class
				.getResourceAsStream("/externs.zip");
		if (input == null) {
			// In some environments, the externs.zip is relative to this class.
			input = CommandLineRunner.class.getResourceAsStream("externs.zip");
		}
		Preconditions.checkNotNull(input);

		ZipInputStream zip = new ZipInputStream(input);
		Map<String, SourceFile> externsMap = Maps.newHashMap();
		for (ZipEntry entry = null; (entry = zip.getNextEntry()) != null;) {
			BufferedInputStream entryStream = new BufferedInputStream(zip);
					// new LimitInputStream(zip, entry.getSize()));
			externsMap.put(entry.getName(), SourceFile.fromInputStream(
			// Give the files an odd prefix, so that they do not conflict
			// with the user's files.
					"externs.zip//" + entry.getName(), entryStream));
		}

		Preconditions.checkState(
				externsMap.keySet().equals(
						Sets.newHashSet(DEFAULT_EXTERNS_NAMES)),
				"Externs zip must match our hard-coded list of externs.");

		// Order matters, so the resources must be added to the result list
		// in the expected order.
		List<SourceFile> externs = Lists.newArrayList();
		for (String key : DEFAULT_EXTERNS_NAMES) {
			externs.add(externsMap.get(key));
		}

		return externs;
	}

}
