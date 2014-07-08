# Volar Video Android SDK
This SDK includes a widget for playing live/archived broadcasts managed by a VolarVideo CMS as well as a wrapper for the API to access the content the Volar system manages.  For detailed documentation on using the SDK, simply open **library/docs/index.html** in a browser.  Below is a short overview on how to integrate the SDK in to your app.

## Usage

*For a working implementation of this project see the `demo-app` folder.*

  1. In Eclipse, include the `library` folder as a local library project or see the [Android Studio](#Android Studio) section for instructions describing how to build an `.aar` library.
  2. In your `AndroidManifest.xml`, ensure that the minimum SDK version is `14`.  You will also need to add an activity as shown here:

        <activity
            android:name="com.volarvideo.mobilesdk.InitActivity"
            android:configChanges="orientation|keyboard|keyboardHidden|navigation|screenSize"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden" />

  3. Include the VVPlayerView widget in your layout like so:

        <com.volarvideo.mobilesdk.vplayer.VVPlayerView
            android:id="@+id/playerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

  4. Lastly, you will need to add the following snippet to the beginning of `onCreate()` in any `Activity` that intends to use `VolarPlayerController`:

        public void onCreate(Bundle b) {
	        super.onCreate(b);
	        /*
	         * Always run checkVolarLibs before using VolarPlayerController or
	         * calling Activity.setContentView() which would result in an
	         * inflated VVPlayerView
	         */
	        if (!LibsChecker.checkVolarLibs(this))
	                return;

            setContentView(R.layout.my_layout);
            VVPlayerView playerView = (VVPlayerView) findViewById(R.id.playerView);
	        VolarPlayerController player = new VolarPlayerController.Builder(context)
	                        .setVmapURI(vmapURL)
	                        .setPlayerView(playerView)
	                        .load();
        }

## Android Studio<a name="Android Studio"></a>
If your project is built with Android Studio, you will need to build an `.aar` file to link to.  You can build an `.aar` file using Gradle.

### If you have gradle installed
from the `/library` folder, in terminal run: `gradle clean build`

### If you want to use the gradle wrapper
from the `/library` folder, in terminal run: `./gradlew clean build`

The final .aar will be created in the `/library/build/outputs/aar/` folder