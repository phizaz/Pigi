import static org.junit.Assert.*;

import org.junit.Test;


public class UtilsTest {

	@Test
	public void testGetIp() {
		assertEquals("", Utils.getNetworkCardsList());
	}
	
	@Test
	public void testGetSubnet() {
		assertEquals("192.168.2", Utils.getSubnet("192.168.2.99"));
	}
	
	@Test
	public void testOs() {
		assertEquals(true, Utils.isMac());
		assertEquals(false, Utils.isWindows());
	}
	
	@Test 
	public void testDownloadFolder() {
		assertEquals("/Users/phizaz/Downloads/", Utils.getDownloadFolder());
	}
}
