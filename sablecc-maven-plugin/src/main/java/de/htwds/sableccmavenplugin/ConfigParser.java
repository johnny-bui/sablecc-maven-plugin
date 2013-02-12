/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwds.sableccmavenplugin;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.logging.Log;

/**
 *
 * @author hbui
 */
public class ConfigParser {

	private final Log logger;
	private final Argument defaultArg;

	public ConfigParser(Log log, Argument defaultAgument) {
		logger = log;
		this.defaultArg = defaultAgument;
	}

	public Argument parseArgument(Map<String, String> m) {
		Argument a = new Argument();
		// parse file
		String file = m.get("file");
		if (file == null) {
			logger.warn("Need exactly one tag <file> in the tag <grammar>");
			return null;
		} else {
			File grammarFile = new File(file);
			if (!grammarFile.isFile()) {
				logger.warn("The config path: " + file + " is not a regular file");
				return null;
			}
		}
		// parse no-inline
		String noInline = m.get("noInline");//.trim().toLowerCase();
		if (noInline != null) {
			noInline = noInline.trim().toLowerCase();
			if (noInline.equals("true")) {
				a.setNoInline();
			}
		} else {
			if (defaultArg.isNoInline()==true) {
				logger.warn("No inline is set by default to false!!!!!!!!!!!");
				a.setNoInline();
			}
		}
		String inlineMaxAlts = m.get("inlineMaxAlts");
		if (inlineMaxAlts != null){
			inlineMaxAlts = inlineMaxAlts.trim().toLowerCase();
			try{
				int max = Integer.parseInt(inlineMaxAlts);
			}catch (NumberFormatException ex){
				logger.warn("The value of the tag <inlineMaxAlts> must be an integer.");
				return null;
			}
		}
		// parse destination
		String destination = m.get("destination");
		if (destination == null) {
			a.setDestination(defaultArg.getDestination());
		} else {
			a.setDestination(destination);
		}
		a.setFile(file);
		return a;
	}
	/*
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
	 */
}
