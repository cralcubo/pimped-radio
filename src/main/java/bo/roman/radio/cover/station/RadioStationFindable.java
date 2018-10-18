package bo.roman.radio.cover.station;

import java.util.Optional;

import bo.roman.radio.cover.model.Radio;

public interface RadioStationFindable {

	Optional<Radio> findRadioStation(String radioName);

	Optional<Radio> getCachedRadio();

}
