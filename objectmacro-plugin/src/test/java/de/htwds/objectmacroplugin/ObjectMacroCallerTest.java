
package de.htwds.objectmacroplugin;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 *
 * @author phucluoi
 * @version Feb 3, 2013
 */
public class ObjectMacroCallerTest extends AbstractMojoTestCase{
	/** {@inheritDoc} */
	@Override
    protected void setUp() throws Exception
    {
        // required
        super.setUp();
    }

	/** {@inheritDoc} */
	@Override
    protected void tearDown() throws Exception
    {
        // required
        super.tearDown();
    }

	/**
     * @throws Exception if any
     */
	/*
    public void testSomething() throws Exception
    {
        File pom = getTestFile( "src/test/resources/unit/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        ObjectMacroCaller myMojo = (ObjectMacroCaller) lookupMojo( "objectmacro", pom );
        assertNotNull( myMojo );
        myMojo.execute();
    }
	*/
}
