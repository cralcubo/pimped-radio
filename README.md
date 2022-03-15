# Pimped Radio

Stable brach
------------
The 'reactive' branch is the branch with the latest modifications, therefore can be considered the code in this branch as the Release Candidate.

What is it?
-----------
This project will implement an internet radio player to be used in a computer (PC, Linux, Mac).
It is called Pimped Radio because this radio player will:
* Look cool, displaying the album art of the currently playing song and information of the playing stream.
* Support of different audio streaming formats.
* Internal data base to store and classify radio stations.

The points mentioned above are the ones that will be released with Version 1.0.1

Technologies to be used
-----------------------
* Java 8
* JUnit/Hamcrest [Testing]
* LastFM [Find CoverArt and Album]
* vlcj [The actual music player]
* ORM Lite for the persistence of a radio stations (More info: https://github.com/cralcubo/pimped-radio-tuner)
* Java FX for the UI (More info: https://github.com/cralcubo/pimped-radio-ui)

Future
-------
Improvements and new features that will be developed on following versions might be:
* Searching of radio stations.
* Add extra information about the playing song such as: artist, link to buy an album and link to a video found in YouTube.
* Support for a remote control through a mobile device.
