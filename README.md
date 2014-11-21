# Volar Video Android SDK
This SDK includes a widget for playing live/archived broadcasts managed by a VolarVideo CMS as well as a wrapper for the API to access the content the Volar system manages.  Detailed documentation about the SDK can be found [here](http://volarvideo.github.io/android-volar-public-framework).  *For a working implementation of this project see the `demo-app` folder.*


### Including the SDK with Eclipse:
  * In Eclipse, go to <b>File</b>-><b>Import</b>-><b>Exisiting Android Code into Workspace</b> and browse to the directory you cloned the <b>android-volar-public-framework</b> repository.  Inside, you will find two project folders.  Import the one in the <b>library</b> folder.  You should now have a project in your package explorer called <b>volar-android-mobilesdk</b>.
  * With your project selected, go to <b>Project</b>-><b>Properties</b>.  On the left, select <b>Android</b>.  In the bottom section, click <b>Add...</b> and select <b>volar-android-mobilesdk</b>.

### Building the SDK for Android Studio:
If your project is built with Android Studio, you will need to build an <b>.aar</b> file to link to. You can build an <b>.aar</b> file using Gradle.

  * If you have gradle installed, from the <b>/library</b> folder, in terminal run: `gradle clean build`
  * If you want to use the gradle wrapper, from the <b>/library</b> folder, in terminal run: `./gradlew clean build`

The final <b>.aar</b> will be crated in the <b>/library/build/outputs/aar/</b> folder

### Using the SDK
Getting started with the SDK is quick and easy.  In your <b>AndroidManifest.xml</b>, ensure that the minimum SDK version is <b>14</b>.

```xml
<uses-sdk
  android:minSdkVersion="14"
  android:targetSdkVersion="16" />
```

To avoid a long load time on the first instance of `VolarPlayerController`, put the following snippet in the `onCreate` of your initial `Activity`:

```java
Volar.getInstance().initialize(this, null);
```

And lastly, here's an example use of `VolarPlayerController`:

```java
public void onCreate(Bundle b) {
  super.onCreate(b);
  setContentView(R.layout.your_layout);

  playerView = (VVPlayerView) findViewById(R.id.playerView);

  // Example player
  VolarPlayerController player = new VolarPlayerController.Builder(context)
    .setVmapURI(vmapURL)
    .setPlayerView(playerView)
    .load();
}
```

In this example, the `VVPlayerView` is assumed to be in the layout xml.  Here's how to include the view in your layout:

```xml
<com.volarvideo.mobilesdk.vplayer.VVPlayerView
  android:id="@+id/playerView"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```

### Querying for Content
To query content on a VolarVideo CMS, you'll need to use the `VVCMSAPI` class.  There are two ways to instantiate this class for authenticated querying.  The first and more desired method is with an API key. You can find a detailed description of how to create an API user <a href="https://github.com/volarvideo/cms-client-sdk/wiki/Creating-api-credentials">here</a>.  Here's an example in code:

```java
String API_KEY = "<your api key>";
VVCMSAPI api = new VVCMSAPI("vcloud.volarvideo.com", API_KEY);
```

The second method of instantiation is with a username and password as shown below: 

```java
VVCMSAPI api = new VVCMSAPI("vcloud.volarvideo.com", "john.doe@test.com", "password");
```

Now that you have an instance of `VVCMSAPI`, it's easy to query for data.  Simply provide an instance of `VVCMSAPIDelegate`to any of the methods to handle responses.  Here's an example of querying for the 3rd page of archived broadcasts with 20 results per page:

```java
BroadcastParams params = new BroadcastParams()
      .status(BroadcastStatus.Archived)
      .page(3)
      .resultsPerPage(20);
api.requestBroadcasts(delegate, params);
```

The corresponding delegate method would look like this:

```java
public void requestForBroadcastsComplete(
  VVCMSAPI api, BroadcastStatus status,
  int page, int totalPages, int totalResults,
  List broadcasts, final Exception e
) {
  // We're not guaranteed to be called on the thread the request was made from
  runOnUiThread(new Runnable() {
      public void run() {
          if(e != null) {
              // handle error
              return;
          }

          // process data
      }
  });
}
```

__NOTE:__ It's important to call `shutdown()` when you are finished with an instance of `VVCMSAPI`. This will prevent any unfinished requests from calling their delegate methods, ignore future requests, and remove the potential for long-lived references to memory.

### Mobile Web Launch
The VolarVideo CMS allows you to register your mobile app to be launched from a mobile browser.  Just follow the steps <a href="https://github.com/volarvideo/cms-client-sdk/wiki/Creating-your-own-Mobile-app">here</a> to get set up.  In this process, you'll choose a custom URL token.  In your <b>AndroidManifest.xml</b>, create an intent filter for your <b>Activity</b> used for video playback.  Below is an example where the token is set to `mytoken`.

```xml
<activity
    android:name=".VideoActivity"
    android:windowSoftInputMode="stateHidden"
    android:exported="true"
    android:hardwareAccelerated="true"
    android:label="@string/app_name"
    android:configChanges="orientation|keyboard|keyboardHidden|navigation|screenSize" >

    <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <category android:name="android.intent.category.DEFAULT" />
         <category android:name="android.intent.category.BROWSABLE" />

         <!-- Custom URL token is specified in the pathPrefix attribute -->
         <!-- "/api/broadcast/vmap:URL_TOKEN_GOES_HERE/" -->
         <data android:pathPrefix="/api/broadcast/vmap:mytoken/" />

         <data android:scheme="http" />
         <data android:scheme="https" />
         <data android:host="*.volarvideo.com" />
    </intent-filter>
</activity>
```

In your <b>VideoActivity</b>, you can detect the web launch and retreive the video URL with the following code snippet:

```java
Uri uri = getIntent().getData();
if (uri != null) {
  String vmapURI = uri.toString();

  // Use vmapURI when creating your VolarPlayerController
}
```
