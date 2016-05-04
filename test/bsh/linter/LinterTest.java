package bsh.linter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class LinterTest {
	/**
	 * Test for a single error in the script. The single error will
	 * be a missing ';'.
	 * @throws IOException
	 */
	@Test
	public void TestScript1() throws IOException {
		FileInputStream fis = new FileInputStream(new File("scripts/Test1.bsh"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		Map<String,String> errors = Linter.lint(br);
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(errors.containsKey("5"));
	}
	
	/**
	 * Test for multiple errors in a script
	 * @throws IOException
	 */
	@Test
	public void TestScript2() throws IOException {
		FileInputStream fis = new FileInputStream(new File("scripts/Test2.bsh"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		Map<String,String> errors = Linter.lint(br);
		Assert.assertEquals(3, errors.size());
		Assert.assertTrue(errors.containsKey("3"));
		Assert.assertTrue(errors.containsKey("8"));
		Assert.assertTrue(errors.containsKey("11"));
	}
	
	/**
	 * Test for no errors in the script
	 * @throws IOException
	 */
	@Test
	public void TestScript3() throws IOException {
		FileInputStream fis = new FileInputStream(new File("scripts/Test3.bsh"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		Map<String,String> errors = Linter.lint(br);
		Assert.assertEquals(0, errors.size());
	}
	
	/**
	 * Test for if-else and do-while statements
	 * @throws IOException
	 */
	@Test
	public void TestScript4() throws IOException {
		FileInputStream fis = new FileInputStream(new File("scripts/Test4.bsh"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		Map<String,String> errors = Linter.lint(br);
		Assert.assertEquals(0, errors.size());
	}
}
