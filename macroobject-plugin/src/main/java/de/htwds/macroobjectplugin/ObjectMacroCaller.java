/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwds.macroobjectplugin;

import com.sun.media.sound.FFT;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Says "Hi" to the user.
 *
 * @phase generate-resources
 */
@Mojo(name = "objectmacro", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ObjectMacroCaller extends AbstractMojo {

	/**
	 * The greeting to display.
	 */
	@Parameter(defaultValue = "Hello World!")
	private String greeting;
	@Parameter(defaultValue = "java")
	private String outputType;
	@Parameter
	private String outDir;
	private final String fileSep = System.getProperty("file.separator");
	private String baseDir = System.getProperty("project.basedir");

	public void execute() throws MojoExecutionException {
		getLog().info(">>>>>>>>>>>>>>>" + greeting + "<<<<<<<<<<<<<<<<<<<");
		getLog().info(">>>>>>>>>>>>>>>" + outputType + "<<<<<<<<<<<<<<<<<<");
		try {
			constructOutDir();
			String outputFile = outDir + "de" + fileSep + "Generate.java";
			
			getLog().info(outputFile);
			File f = new File(outputFile);
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f,false);
			fw.write("package de;\n");
			fw.append("public class Generate{ public void hi(){System.out.println(\"hi\");}}\n");
			fw.close();
		} catch (IOException ex) {
			throw new MojoExecutionException("Write to file error", ex);
		}


	}

	private String constructOutDir() {
		if (baseDir == null) {
			baseDir = ".";
		}
		if (outDir == null) {
			outDir = baseDir + fileSep
					+ "target" + fileSep
					+ "generated-sources" + fileSep
					+ "objectmacro" + fileSep;
		} else {
			if (!outDir.endsWith(fileSep)) {
				outDir += fileSep;
			}
		}
		return outDir;
	}
}