package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Paths;

import org.junit.Test;

public class ImageUtilTest {
	
	private static final String ROOT_PATH = "src/test/resources/images/";
	private static final String SMALLIMAGE_PATH = ROOT_PATH + "200.jpeg";
	private static final String BIGIMAGE_PATH = ROOT_PATH + "500.jpeg";
	
	@Test
	public void testIsBigEnough_BigImage() {
		assertThat(ImageUtil.isBigEnough(Paths.get(BIGIMAGE_PATH).toUri().toString()), is(true));
	}
	
	@Test
	public void testIsBigEnough_SmallImage() {
		assertThat(ImageUtil.isBigEnough(Paths.get(SMALLIMAGE_PATH).toUri().toString()), is(false));
	}
	
	@Test
	public void testIsBigEnough_noImage() {
		assertThat(ImageUtil.isBigEnough(""), is(false));
	}
	
	@Test
	public void testIsBigEnough_notExistentUri() {
		assertThat(ImageUtil.isBigEnough("file:///a/path"), is(false));
	}

}
