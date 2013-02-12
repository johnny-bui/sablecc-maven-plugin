package de.htwds.sableccmavenplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author phucluoi
 * @version Feb 3, 2013
 */
public class Argument {
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

	private List<String> argv;
	private String d;               // default: null
	private boolean noInline;              // default: true
	private int m;                  // default: 20 (S. SableCC source)
	private String f;
	
	public Argument(){
		argv = new ArrayList<String>();
		noInline = false;
		m = 20;
	}
	

	public String[] getStringArgv(){
		return argv.toArray(new String[0]);
	}

	public List<String> getArgv(){
		List<String> clone = new ArrayList<String>(argv.size());
		for(String c : argv){
			clone.add(c);
		}
		return clone;
	}
	
	public void setDestination(String directory) {
		d = directory;
		File f = new File(d);
		if (!f.exists()){
			f.mkdirs();
		}
		argv.add("-d");
		argv.add(f.getAbsolutePath());
	}
	
	public void setFile(String fileName){
		f = fileName;
		argv.add(fileName);
	}
	
	public String getFile(){
		return f;
	}
	
	public String getDestination(){
		return d;
	}	
	
	public void setNoInline(){
		noInline = true;
		argv.add("--no-inline");
	}

	// default: true
	public boolean isNoInline(){
		return noInline;
	}

	public void setInlineMaxAlts(int max){
		m = max;
	}

	public int getInlineMaxAlts(){
		return m;
	}
	
	public static Argument createDefaultArgument(String destination, boolean isNoInline, int maxAlts){
		Argument a = new Argument();
		// set default output dir
		String directory = constructOutDir(destination);
		a.setDestination(directory);
		if(isNoInline){
			a.setNoInline();
		}else{
			a.setProcessInline();
		}
		a.setInlineMaxAlts(maxAlts);		
		return a;
	}
	
	private static String constructOutDir(String directory) {
		String baseDir = System.getProperty("project.basedir");
		String fileSep = System.getProperty("file.separator");
		if (baseDir == null) {
			baseDir = ".";
		}
		String outDir = null;
		if (directory == null) {
		outDir = baseDir + fileSep
					+ "target" + fileSep
					+ "generated-sources" + fileSep
					+ "sablecc" + fileSep;
		} else {
			if (!directory.endsWith(fileSep)) {
				outDir = directory + fileSep;
			}
		}
		return outDir;
	}		

	private void setProcessInline() {
		noInline = false;
	}

	
	
}
