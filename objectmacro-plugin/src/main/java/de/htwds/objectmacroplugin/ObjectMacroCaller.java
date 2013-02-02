package de.htwds.objectmacroplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sablecc.objectmacro.launcher.ObjectMacro;

/**
 * Call ObjectMacro to generate Java file from ObjectMacro file.
 *
 * @author Hong Phuc Bui
 * @version 1.0-SNAPSHOT
 *
 * @phase generate-resources
 */
@Mojo(name = "objectmacro", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ObjectMacroCaller extends AbstractMojo {

	/**
	 * Output Type (Java, etc... depends on ObjectMacro)
	 */
	@Parameter(defaultValue = "java")
	private String language;
	@Parameter
	private String directory;
	@Parameter(defaultValue = "template")
	private String packagename;
	/**
	 * true => --generate-code, false => --no-code.
	 */
	@Parameter(defaultValue = "true")
	private boolean generateCode;
	/**
	 * true => strict false => lenient.
	 */
	@Parameter(defaultValue = "true")
	private boolean strict;
	/**
	 * informative => --informativ quite => --quiet verbose => --verbose.
	 */
	@Parameter(defaultValue = "informative")
	private String informative;
	@Parameter
	private List<Map> templates;
	private final String fileSep = System.getProperty("file.separator");
	private String baseDir = System.getProperty("project.basedir");
	private static final String GEN_CODE = "--generate-code";
	private static final String NO_CODE = "--no-code";
	private static final String STRICT = "--strict";
	private static final String LENIENT = "--lenient";
	private static final String INFORMATIVE = "--informative";
	private static final String VERBOSE = "--verbose";
	private static final String QUIET = "--quiet";
	private static final Set<String> packageNameNoGo = new HashSet<String>();
	static {// TODO add more 
		packageNameNoGo.add("..");
		packageNameNoGo.add("#");
		packageNameNoGo.add("@");
		packageNameNoGo.add("?");
		packageNameNoGo.add("/");
		packageNameNoGo.add("%");
		packageNameNoGo.add(" ");
	}

	public void execute() throws MojoFailureException {
		try {
			constructOutDir();
			for (Map m : templates) {
				List<String> argv = parseArgument(m);
				if (argv != null) {
					getLog().info("call ObjectMacro with argv:");
					//for (String s : argv) {
					//	getLog().info(s);
					//}
					getLog().info(argv.toString());
					ObjectMacro.compile(argv.toArray(new String[0]));
				}
			}
		} catch (RuntimeException ex) {
			throw new MojoFailureException("Compile template file error: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new MojoFailureException("Compile template file error: " + ex.getMessage(), ex);
		}

	}

	private List<String> parseArgument(Map m) {
		List<String> arg = new ArrayList<String>();
		// the template file
		// TODO: optimize here, check the tag <file> first.
		String file = (String) m.get("file");
		if (file == null) {
			getLog().warn("Configuration fail, cannot find the tag <file>");
			return null;
		} else {
			if (isFileNameValid(file)) {
				// option "-t language"
				String l = (String) m.get("language");
				String localLanguage = (isOptionValid(l)) ? l.trim() : language;
				arg.add("-t");
				arg.add(localLanguage);
				// option "-d directory" // TODO: check validation directory
				String d = (String) m.get("directory");
				String localDirectory = (isOptionValid(d)) ? d.trim() : directory;
				arg.add("-d");
				arg.add(localDirectory);
				// option "-p packagesname"
				String p = (String) m.get("packagename");
				if (p != null) {
					if (isPackageNameValid(p)) {
						String localPackagename = p.trim();
						arg.add("-p");
						arg.add(localPackagename);
					} else {
						throw new RuntimeException("package name not valid:" + p.trim());
					}
				} else {
					if (isPackageNameValid(packagename)) {
						String localPackagename = packagename.trim();
						arg.add("-p");
						arg.add(localPackagename);
					} else {
						throw new RuntimeException("package name not valid:" + packagename.trim());
					}
				}
				// option "--generate-code" or "--no-code"
				String localGenerateCode = generateCode ? GEN_CODE : NO_CODE;
				String g = (String) m.get("generateCode");
				if (g != null) {
					g = g.trim().toLowerCase();
					localGenerateCode = (g.equals("true") || g.equals("generate-code")) ? GEN_CODE : NO_CODE;
				}
				arg.add(localGenerateCode);
				// option "--strict" or "--lenient"
				String localStrict = strict ? STRICT : LENIENT;
				String s = (String) m.get("strict");
				if (s != null) {
					s = s.trim().toLowerCase();
					localStrict = (s.equals("true") || s.equals("strict")) ? STRICT : LENIENT;
				}
				arg.add(localStrict);
				// option "--quiet" or "--informative" or "--verbose"
				String localInformative = INFORMATIVE;
				String i = (String) m.get("informative");
				if (i != null) {
					i = i.trim().toLowerCase();
					if (i.equals("quiet")) {
						localInformative = QUIET;
					} else if (i.equals("verbose")) {
						localInformative = VERBOSE;
					} else if (i.equals("informative")) {
						localInformative = INFORMATIVE;
					}
				} else {
					informative = informative.trim().toLowerCase();
					if (informative.equals("quiet")) {
						localInformative = QUIET;
					} else if (informative.equals("informative")) {
						localInformative = INFORMATIVE;
					} else if (informative.equals("verbose")) {
						localInformative = VERBOSE;
					}
				}
				arg.add(localInformative);
				arg.add(file.trim());
			} else {
				getLog().warn("Configuration fail, file name: " + file + " invalid");
				return null;
			}
		}
		// return the result
		//String[] argv = arg.toArray(new String[0]);
		return arg;
	}

	private String constructOutDir() {
		if (baseDir == null) {
			baseDir = ".";
		}
		if (directory == null) {
			directory = baseDir + fileSep
					+ "target" + fileSep
					+ "generated-sources" + fileSep
					+ "objectmacro" + fileSep;
		} else {
			if (!directory.endsWith(fileSep)) {
				directory += fileSep;
			}
		}
		return directory;
	}

	boolean isOptionValid(String option) {
		if (option == null) {
			return false;
		}
		if (option.trim().length() == 0) {
			return false;
		}
		return true;
	}

	boolean isPackageNameValid(String pn) {
		if (!isOptionValid(pn)) {
			return false;
		}
		if (pn.trim().startsWith(".")) {
			return false;
		}
		for (String n : packageNameNoGo) {
			if (pn.contains(n)) {
				return false;
			}
		}
		return true;
	}

	// TODO: make more restrict here
	private boolean isFileNameValid(String file) {
		assert file != null;
		return true;
	}
}