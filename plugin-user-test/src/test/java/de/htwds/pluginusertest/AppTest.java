package de.htwds.pluginusertest;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mysqlgrm.lexer.Lexer;
import mysqlgrm.lexer.LexerException;
import mysqlgrm.parser.Parser;
import mysqlgrm.parser.ParserException;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

	public void testMySQLParser() throws ParserException, LexerException, IOException{
		Parser p = new Parser(new Lexer(new PushbackReader(new StringReader("create table xxx(a integer);"))));
		p.parse();	
	}
}
