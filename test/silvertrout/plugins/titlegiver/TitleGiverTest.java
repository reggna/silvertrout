package silvertrout.plugins.titlegiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;


public class TitleGiverTest {

	//TitleGiver tg;
	
	@Before
	public void setUp() throws Exception {
		//tg = new TitleGiver();
	}
	
	private void runTest(Pattern p, String host) {
		Matcher m = p.matcher(host);
		assertTrue("Base: " + host + "\nPatter: " + p.toString(), m.matches());
		assertEquals("Group: " + m.group(), host, m.group());
	}
	
	private void runNonTest(Pattern p, String host) {
		Matcher m = p.matcher(host);
		assertFalse(m.matches() ? "Group " + m.group() : "", m.matches());
	}
	
	@Test
	public void testCreateProtocolPattern() {
		System.out.println("Protocol pattern: " + TitleGiver.createProtocolPattern());
		Pattern p = Pattern.compile(TitleGiver.createProtocolPattern());
		Matcher m = p.matcher("http://");
		assertTrue(p.toString(), m.matches());
		assertEquals("Group: " + m.group(), "http://", m.group());
		
		m = p.matcher("https://");
		assertTrue(p.toString(), m.matches());
		assertEquals("Group: " + m.group(), "https://", m.group());
		
		// TODO maybe check that the pattern doesn't include to much
	}
	
	@Test
	public void testCreateHostPattern() {
		System.out.println("Host pattern: " + TitleGiver.createHostPattern());
		Pattern p = Pattern.compile(TitleGiver.createHostPattern());
		
		runTest(p, "www.google.com");
		runTest(p, "www.göteborg.se");
		runTest(p, "wiki.answers.com");
		runTest(p, "stenbacka.mine.nu");
		runTest(p, "åäö.abc.com");
		runTest(p, "www.cse.chalmers.se");
		
		runNonTest(p, "http://");
		runNonTest(p, "www.cse.chalmers.se:8080");
	}

	@Test
	public void testCreatePortPattern() {
		System.out.println("Port pattern: " + TitleGiver.createPortPattern());
		Pattern p = Pattern.compile(TitleGiver.createPortPattern());
		Matcher m = p.matcher("");
		assertTrue(p.toString(), m.matches());
		assertEquals("Group: " + m.group(), "", m.group());
		
		m = p.matcher(":80");
		assertTrue(p.toString(), m.matches());
		assertEquals("Group: " + m.group(), ":80", m.group());
		
		m = p.matcher(":8080");
		assertTrue(p.toString(), m.matches());
		assertEquals("Group: " + m.group(), ":8080", m.group());
		
		m = p.matcher(":8a080");
		assertFalse(m.matches() ? "Group " + m.group() : "", m.matches());
		m = p.matcher("a");
		assertFalse(m.matches() ? "Group " + m.group() : "", m.matches());
	}
	
	@Test
	public void testCreatePathPattern() {
		System.out.println("Path pattern: " + TitleGiver.createPathPattern());
		Pattern p = Pattern.compile(TitleGiver.createPathPattern());
		runTest(p, "");
		runTest(p, "/");
		runTest(p, "/help");
		runTest(p, "/help/me/now");
		runTest(p, "/x.html?q=er");
		runTest(p, "/x.html?q=er&z=2");
		runTest(p, "/q/w/e.aspx?");
		runTest(p, "/~skola/");
		
		runNonTest(p, "http://");
		runNonTest(p, "www.cse.chalmers.se:8080");
	}

	@Test
	public void testCreatePattern() {
		System.out.println("Complete pattern: " + TitleGiver.createURLPattern());
		Pattern p = Pattern.compile(TitleGiver.createURLPattern());
		
		runTest(p, "http://www.google.com");
		runTest(p, "http://www.google.com/help");
		runTest(p, "http://www.google.com/help/foo");
		runTest(p, "http://www.cse.chalmers.se/~feldt/courses/reqeng/");
		runTest(p, "https://www.cse.chalmers.se/~feldt/courses/reqeng/");
		runTest(p, "https://www.cse.chalmers.se:8080/~feldt/courses/reqeng/");
		runTest(p, "http://www.google.se/search?q=java6+javadoc&ie=utf-8&oe=utf-8&aq=t&rls=com.ubuntu:en-US:official&client=firefox-a");
		runTest(p, "http://www.google.se/search?q=%26");
		runTest(p, "http://göteborg.se/hj_er");

		
		runNonTest(p, "http://www.google.com//help");
		runNonTest(p, "http://www.google.com//");
		runNonTest(p, "http://w.senews+");
		runNonTest(p, "http://.");
    
        // Delimiter tests
        Matcher m;
        String base = "http://www.google.com";
        String s;

        s = "<" + base + ">";
        m = p.matcher(s);
        assertTrue("Base: " + s + "\nPatter: " + p.toString(), m.matches());
        assertEquals("Group: " + m.group(), s, m.group());
        assertEquals("Group: " + m.group(2), base, "http://" + m.group(2));

        s = base + "#foo";
        m = p.matcher(s);
        assertTrue("Base: " + s + "\nPatter: " + p.toString(), m.matches());
        assertEquals("Group: " + m.group(), s, m.group());
        assertEquals("Group: " + m.group(2), base, "http://" + m.group(2));

        s = base + "#";
        m = p.matcher(s);
        assertTrue("Base: " + s + "\nPatter: " + p.toString(), m.matches());
        assertEquals("Group: " + m.group(), s, m.group());
        assertEquals("Group: " + m.group(2), base, "http://" + m.group(2));

        s = base + "/bar#";
        m = p.matcher(s);
        assertTrue("Base: " + s + "\nPatter: " + p.toString(), m.matches());
        assertEquals("Group: " + m.group(), s, m.group());
        assertEquals("Group: " + m.group(2), base, "http://" + m.group(2));

        s = "\"" + base + "\"";
        m = p.matcher(s);
        assertTrue("Base: " + s + "\nPatter: " + p.toString(), m.matches());
        assertEquals("Group: " + m.group(), s, m.group());
        assertEquals("Group: " + m.group(2), base, "http://" + m.group(2));
	}
}
