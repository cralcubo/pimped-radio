package bo.roman.radio.cover.model;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class AlbumTest {
	
	@Test
	public void testEqual() {
		Album a1 = new Album.Builder().name("name").artistName("artist").mbid("1").build();
		Album a2 = new Album.Builder().mbid("1").build();
		
		// Albums with the same mbid are equal
		assertThat(a1, is(equalTo(a2)));
	}
	
	@Test
	public void testNotEqual() {
		Album a1 = new Album.Builder().name("name").artistName("artist").mbid("1").build();
		Album a2 = new Album.Builder().name("name").artistName("artist").mbid("2").build();
		
		// Albums with different mbid are not equal
		assertThat(a1, is(not(equalTo(a2))));
	}

}
