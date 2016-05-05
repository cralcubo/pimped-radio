package bo.roman.radio.player.codec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.player.model.CodecInformation;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_stats_t;
import uk.co.caprica.vlcj.player.AudioTrackInfo;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.player.TrackType;
import uk.co.caprica.vlcj.player.VideoTrackInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AudioTrackInfo.class)
public class CodecCalculatorTest {
	
	@Mock
	private MediaPlayer mediaPlayer;
	
	@Mock
	private VideoTrackInfo videoInfo;
	
	private AudioTrackInfo audioInfo;
	
	@Before
	public void setUp() {
		audioInfo = PowerMockito.mock(AudioTrackInfo.class);
	}
	
	@Test
	public void testBitRateAvailable() {
		int testBitRate = 128_000;
		String testCodecName = "aName";
		int testChannels = 2;
		int testSampleRate = 44100;
		
		// Prepare Mock
		prepareAudioInfoMock(testBitRate, testCodecName, testChannels, testSampleRate);
		
		Optional<CodecInformation> oInfo = CodecCalculator.calculate(mediaPlayer);
		
		// Assertions
		assertThat(oInfo.isPresent(), is(true));
		CodecInformation ci = oInfo.get();
		float bitRateExp = testBitRate / 1000;
		assertThat(ci.getBitRate(), is(bitRateExp));
		assertThat(ci.getCodec(), is(testCodecName));
		assertThat(ci.getChannels(), is(testChannels));
		assertThat(ci.getSampleRate(), is(44.1f));
	}
	
	@Test
	public void testCalculateBitRate() {
		int testBitRate = 0;
		String testCodecName = "aName";
		int testChannels = 2;
		int testSampleRate = 44100;
		
		// Prepare Mock
		prepareAudioInfoMock(testBitRate, testCodecName, testChannels, testSampleRate);
		
		// First check if there is a bitRate to return
		libvlc_media_stats_t stats = new libvlc_media_stats_t();
		stats.f_demux_bitrate = 0.0f;
		
		// We enter in the loop, the bitRate will be calculated in 
		// the third iteration.
		libvlc_media_stats_t stats1 = new libvlc_media_stats_t();
		stats1.f_demux_bitrate = 0.0f;
		stats1.i_demux_read_bytes = 76498;
		
		libvlc_media_stats_t stats2 = new libvlc_media_stats_t();
		stats2.f_demux_bitrate = 0.07173221f;
		stats2.i_demux_read_bytes = 117129;
		
		libvlc_media_stats_t stats3 = new libvlc_media_stats_t();
		stats3.f_demux_bitrate = 0.039846577f;
		stats3.i_demux_read_bytes = 157631;
		when(mediaPlayer.getMediaStatistics()).thenReturn(stats, stats1, stats2, stats3);

		Optional<CodecInformation> oInfo = CodecCalculator.calculate(mediaPlayer);

		// Assertions
		assertThat(oInfo.isPresent(), is(true));
		
		CodecInformation ci = oInfo.get();
		assertThat(ci.getBitRate() >= 310 && ci.getBitRate() <= 330, is(true));
		
		assertThat(ci.getCodec(), is(testCodecName));
		assertThat(ci.getChannels(), is(testChannels));
		assertThat(ci.getSampleRate(), is(44.1f));
	}
	
	@Test
	public void testPlayerStartedCodec() {
		
	}
	
	@Test
	public void testPlayerRunningCodec() {
		
	}
	
	@Test
	public void testNoMediaPlayer() {
		assertThat("No MediaPlayer, then no CodecInfo expected.", CodecCalculator.calculate(null).isPresent(), is(false));
	}
	
	@Test
	public void testNoAudioTrackInfo() {
		// Prepare mock
		List<TrackInfo> infos = Arrays.asList(videoInfo);
		when(mediaPlayer.getTrackInfo(TrackType.AUDIO)).thenReturn(infos);
		
		Optional<CodecInformation> oInfo = CodecCalculator.calculate(mediaPlayer);
		assertThat("No AudioTrackInfo found then no CodecInfo expected.", oInfo.isPresent(), is(false));
	}
	
	/* *** Utilities *** */
	private void prepareAudioInfoMock(int bitRate, String codecName, int channels, int sampleRate) {
		// Prepare Mock
		List<TrackInfo> infos = Arrays.asList(audioInfo);
		when(mediaPlayer.getTrackInfo(TrackType.AUDIO)).thenReturn(infos);
		
		PowerMockito.when(audioInfo.bitRate()).thenReturn(bitRate);
		PowerMockito.when(audioInfo.channels()).thenReturn(channels);
		PowerMockito.when(audioInfo.codecName()).thenReturn(codecName);
		PowerMockito.when(audioInfo.rate()).thenReturn(sampleRate);
	}
	

}
