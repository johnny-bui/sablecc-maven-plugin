
package de.htwds.sableccmavenplugin;

import java.io.File;

/**
 * Can use a Strategy Pattern to make the check of valid grammar file and
 * destination directory more scalable and finer, but it is maybe overkill here.
 * 
 * @author phucluoi
 * @version Feb 23, 2013
 */
public class ArgumentVerifier {
	
	private String destination;
	private String grammar;
	

	public final String verifyGrammarPath(String grammar) throws RuntimeException{
		File grammarFile = new File(grammar);
		if (!grammarFile.exists()){
			throw new RuntimeException("The grammar file " + grammar + " does not exist");
		}

		if (grammarFile.isDirectory()){
			throw new RuntimeException("The path " + grammar + " is a directory");
		}

		this.grammar = grammarFile.getAbsolutePath();
		return grammar;
	}

	public final String verifyDestinationPath(String dir) throws RuntimeException{
		File destinationDir = new File(dir);
		if (!destinationDir.exists()){
			if (!destinationDir.mkdirs()){
				throw new RuntimeException("Cannot make dirs " + dir);
			}
		}else if (destinationDir.isFile()){
			throw new RuntimeException("The path " + dir + " is a file");
		}
		destination = destinationDir.getAbsolutePath();
		if (destination==null){
			throw new RuntimeException(">>>>>>>>>>>> WHYYYYYYYYYYYYYYYYYYYY");
		}
		return destination;
	}
}
