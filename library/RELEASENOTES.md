## Release Notes

### Known Issues:
* Hardware decoders can intermittently fail (need more robust error handling -- fallback to software codecs)
* Intermittent video load failure (can result in "Ads nots available" or "FFmpegException")
* Seeking after bitrate switches can result in the current time to be offset by a large number
* Geoblocked broadcasts can result in endless loading if gps is on but can't find a location
    - Suggested solution (have a timeout and display a dialog that takes you to gps settings to turn on wifi access)

### v0.1.05
* Adds hardware acceleration support (enables 720p on most devices)
* Enabled automatic bitrate switching
* Adds preoll support
* Adds audio-only splash screen
* Fixed memory leak which caused crash on Galaxy Tab 3
* Fixed play/pause button getting out of sync with active player
* Pause state is remembered when seeking
* Fixed broadcast dates in BroadcastsActivity
* Fixed miscellaneous bugs


### v0.1.04
* Adds support for geoblocking
* Fixed audio problem where content and ad players play simultaneously
* Adds support for forced ad breaks
* Fixed bug where, after playing several ads, Android would fail to create an AudioTrack with the message "no more track names available"
* Player now waits for the splash image to download before finishing loading
* Revamped cuepoint implementation
    - Fixes multiple firing of cuepoints when seeking near the start of an adbreak
    - Fixes bug where shutting down could leave a rogue timer that starts an ad
* Misc concurrency problems

### v0.1.03
* Fixed "window leaked" bug in InitActivity
* Documentation

### v0.1.02
* Aspect ratio button works as intended
* Fixed heap corruption when instatiating ad players
* Hitting "Back" during an ad now stops the playing ad correctly
* Spinner now correctly hides during ad breaks
* SDK now allows VVCMSAPI to initialize with a site slug

### v0.1.01
* Adds audio-only support
* Adds SDK version to QoS overlay