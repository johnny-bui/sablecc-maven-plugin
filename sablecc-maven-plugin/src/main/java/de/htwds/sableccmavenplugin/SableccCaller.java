package de.htwds.sableccmavenplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProjectHelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.sablecc.sablecc.SableCC;

/**
 * Call ObjectMacro to generate Java file from ObjectMacro file.
 *
 * @author Hong Phuc Bui
 * @version 1.0-SNAPSHOT
 *
 * @phase generate-resources
 */
@Mojo(name = "sablecc", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SableccCaller extends AbstractMojo {
	
	@Parameter()
	private String destination ;
	
	@Parameter(defaultValue="false")
	private boolean noInline;

	@Parameter(defaultValue="20")
	private int inlineMaxAlts;	
	
	@Parameter(required=true)
	private List<Map> grammars;


	
	@Parameter(defaultValue="${component.org.apache.maven.project.MavenProjectHelper}")
	private MavenProjectHelper projectHelper;
	
	@Parameter(defaultValue="${project}")
	private MavenProject project;
	
	@Override
	public void execute() throws MojoFailureException {
		try {
			if (projectHelper==null){
				projectHelper = new DefaultMavenProjectHelper();
			}
			if (project == null){
				getLog().warn("project is null");
			}
			if (noInline){
				getLog().warn("--no-inline is set by default to TRUE !!!!!!!!!!!");
			}
			Argument defaultArgument = Argument.createDefaultArgument(destination, noInline, inlineMaxAlts);
			Log log = getLog();
			ConfigParser p = new ConfigParser(log, defaultArgument);
			if (grammars != null){
				Set<String> dirs = new HashSet<String>();
				for (Map m : grammars) {
					Argument argv = p.parseArgument(m);
					if (argv != null) {
						getLog().info("call SableCC with argv:");
						getLog().info(argv.getArgv().toString());
						try{
							// TODO: because the method SableCC.main(String[] argv)
							// does not throw any exception to tell/signal the Client
							// but just calls System.exit(1) for any error, I can not
							// use these method to conpile the grammar file. Therefore
							// these options don't take any effect:
							// --no-inline
							// --inline-max-alts
							SableCC.processGrammar(argv.getFile(), argv.getDestination());
							dirs.add(argv.getDestination());
							projectHelper.addResource( project, argv.getDestination(), 
									Collections.singletonList("**/**.dat"), new ArrayList() );
						}catch(Exception ex){
							log.error("Cannot compile the file " + argv.getFile());
							log.error(ex.getMessage());
							throw new MojoFailureException("Cannot compile the file " + argv.getFile(), ex);
						}
					}
				}
				for(String d: dirs){
					getLog().info("add " + d + " to resources");
					project.addCompileSourceRoot(d);
				}
			}else{
				//TODO: What is the convenient behavior if there are not 
				// templated files? I just put an warning out on screen.
				getLog().warn("no tag <grammars> found");
			}
		} catch (RuntimeException ex) {
			throw new MojoFailureException("Compile grammar file error: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new MojoFailureException("Compile grammar file error: " + ex.getMessage(), ex);
		}
	}
}