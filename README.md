maven-plugin
============

Maven plugin to call sablecc from within maven.

* `sablecc-maven-plugin` Plugin to call SableCC from within a maven project
* `sablecc-plugin-user-test` demonstration usage of this plugin (for user: pls. see the file `pom.xml`: 
https://github.com/johnny-bui/sablecc-maven-plugin/blob/master/plugin-user-test/pom.xml ).


New in version 2.0-beta.5

* If the SableCC file is in the directory `${basedir}/src/main/sablecc` and its name does not contain
	the File-Separtor character	then the configuration <grammar> has to be only the file name without path
	eg: `MySQLGrammar.scc` like the example POM in `sablecc-plugin-user-test`


