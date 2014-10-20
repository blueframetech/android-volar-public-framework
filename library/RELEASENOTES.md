## Release Notes

### Known Issues
* Intermittent crash after 20-30 minutes of continuous ads

### v0.2.01-h1
* Better handling of corrupt video resources

### v0.2.01
* Added custom player user-agent
* Fixed NPE crash caused by race condition when shutting down
* Greatly optimized the loading of the player by removing unnecessary VAST loads
* Fixed bug where midrolls could fail to load when there were no prerolls

### v0.2.00-h1
* Replaced maxmind SDK with internal REST call
* Removed the need for several dependencies including jackson

### v0.2.00
* Adds real-time analytics
* Fixed occurrences of "FFmpegException: XYZ"
* Player no longer hangs on the last frame of prerolls
* Player is now more robust against broadcasts that have just started streaming
* Implements phase 1 of optimizing preroll loading
* Support for web-launch v2.0 (CMS support pending)
* Fixed miscellaneous crashes

### v0.1.09-h1
* Added sectionID to VVCMSBroadcast
* Added option to VVPlayer to hide scale mode button
* Fixed crash that could happen when seeking
* Optimized initial load of player

### v0.1.09
* VVCMSAPI now supports both the mobile API key and username/password authentication
* Greatly enhanced the VVCMSAPI interface to query content
* The SDK now handles keeping the screen on during video playback
* Fixed miscellaneous bugs

### v0.1.08
* Adds live DVR support
* Adds slow-mo support
* Adds 15s rewind button
* Fixed portrait mode in VVPlayerView
* Adds Support for custom scheme webstart
* Added pagination parameters to VVCMSAPI delegate methods
* Fixed miscellaneous bugs

### v0.1.07
* Broadcasts now start up on their own after joining a stopped stream
* Fixed bug for broadcasts that start in audio-only
* Added timeout for GPS requests (8 seconds)
* Archived broadcasts that use pre-broadcast test mode now correctly display the current play time
* Technical difficulties is no longer falsely conveyed to the user
* Fixed intermittent buffering deadlock in extremely poor network conditions
* Fixed intermittent issue on the Nexus 7 where image could be corrupt after a midroll adbreak
* Better handling of live midrolls when ads aren't available
* Fixed problem where the first frame after a seek was getting flushed


### v0.1.06
* Fixed problems with hardware decoders intermittently failing
* Seeking after bitrate switches can no longer result in the current time to be
  offset by a large number
* Adds support for multiple weighted VASTs
* New preroll logic (count / duration)
* Live tab of demo-app now displays stopped broadcasts
* Fixed large memory leak which resulted in crash on all devices
* Fixed crash when tapping resize button on geo-blocked broadcasts
* Fixed miscellaneous bugs

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