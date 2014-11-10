package com.github.verylazyboy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.project.MavenProjectHelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.sablecc.sablecc.SableCC;

/**
 * Call SableCC to generate Java file from ObjectMacro file.
 *
 * @author Hong Phuc Bui
 * @version 2.0-SNAPSHOT
 *
 * @phase generate-resources
 */
@Mojo(name = "sablecc", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class SableccCaller extends AbstractMojo {

	/**
	 * where to write the generated parser. Default:
	 * {@code ${basedir}/target/generated-sources/sablecc}
	 */
	@Parameter(defaultValue = "${basedir}/target/generated-sources/sablecc")
	private String destination;
	@Parameter(defaultValue = "false")
	private boolean noInline;
	@Parameter(defaultValue = "20")
	private int inlineMaxAlts;
	@Parameter(required = false, defaultValue = "")
	private String outputPackage;
	@Parameter(required = true)
	private String grammar;
	@Parameter(defaultValue = "${component.org.apache.maven.project.MavenProjectHelper}")
	private MavenProjectHelper projectHelper;
	@Parameter(defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(defaultValue = "${basedir}/src/main/sablecc")
	private String sableccDirPath;
	
	@Override
	public void execute() throws MojoFailureException {
		try {
			if (projectHelper == null) {
				projectHelper = new DefaultMavenProjectHelper();
			}
			if (project == null) {
				getLog().warn("project is null");
			}
			if (noInline) {// this warning will be removed when I can set this option
				getLog().warn("--no-inline is set by default to TRUE !!!!!!!!!!!");
			}
			Set<String> dirs = new HashSet<String>();
			try {
				// TODO: because the method SableCC.main(String[] argv)
				// does not throw any exception to tell/signal the Client
				// but just calls System.exit(1) for any error, I can not
				// use these method to conpile the grammar file. Therefore
				// these options don't take any effect:
				// --no-inline
				// --inline-max-alts
				ArgumentVerifier arg = new ArgumentVerifier();
				File grammarFile = guessSableCCFile(grammar);
				String validedGrammarPath 
						= arg.verifyGrammarPath(grammarFile.getAbsolutePath());
				File destinateDir = new File(destination);
				if(!destinateDir.isAbsolute()){
					destinateDir = new File(project.getBasedir(), destination);
				}
				String validedDirPath = arg.verifyDestinationPath(destinateDir.getAbsolutePath());
				if (neeedCompile(validedGrammarPath, validedDirPath)) {
					getLog().debug("Need to compile grammar " + validedGrammarPath);
					SableCC.processGrammar(validedGrammarPath, validedDirPath);
				} else {
					getLog().info("Not need to compile " + validedGrammarPath);
					getLog().info("Clean output directory to force re-compile the grammar file " + validedGrammarPath);
				}
				dirs.add(validedDirPath);
				projectHelper.addResource(project, validedDirPath,
						Collections.singletonList("**/**.dat"), new ArrayList());
			} catch (Exception ex) {
				getLog().error("Cannot compile the file " + grammar);
				getLog().error(ex.getMessage());
				throw new MojoFailureException("Cannot compile the file " + grammar, ex);
			}
			for (String d : dirs) {
				getLog().info("add " + d + " to generated source files");
				project.addCompileSourceRoot(d);
			}

		} catch (RuntimeException ex) {
			throw new MojoFailureException("Compile grammar file error: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new MojoFailureException("Compile grammar file error: " + ex.getMessage(), ex);
		}
	}

	private File guessSableCCFile(String grammarConfigParam){
		if (grammarConfigParam.contains(File.separator)){
			File grammarFile = new File(grammarConfigParam);
			if (!grammarFile.isAbsolute()) {
				grammarFile = new File(project.getBasedir(), grammarConfigParam);
			}
			return grammarFile;
		}else{
			File sableccDir = new File(sableccDirPath);
			File grammarFile = new File(sableccDir,grammarConfigParam);
			return grammarFile;
		}
	}	
	
	private boolean neeedCompile(String grammar, String destination) {
		if (outputPackage == null || outputPackage.trim().length() == 0) {
			getLog().info("No output package given or the given outputPackage is an empty string");
			getLog().info(" Cannot calcualte time stame");
			getLog().info(" Grammar will be recompiled");
			return true;
		} else {
			String generatedParserPath =
					destination
					+ "/"
					+ outputPackage.replace(".", "/")
					+ "/parser/Parser.java";
			getLog().debug("Check time stamp for the file:" + generatedParserPath);
			File parserFile = new File(generatedParserPath);
			if (parserFile.isFile()) {// if the parser file exists
				long parserLastModi = parserFile.lastModified();
				getLog().debug("*********************** Last modi time of parser:" + parserLastModi);
				File grammarFile = new File(grammar);
				long grammarLastModi = grammarFile.lastModified();
				getLog().debug("*********************** Last modi time of grammar:" + grammarLastModi);
				if (grammarLastModi > parserLastModi) {// the grammar file older than the parser file
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}

		}
	}
}