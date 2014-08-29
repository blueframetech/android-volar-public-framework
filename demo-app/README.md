# demo-app
This android project is a demo application using the VolarVideo android SDK.  To view your content you have to authenticate using the `VVCMSAPI` class. To get the demo application to authenticate, open the `MediaListActivity.java` file.  Find the following line:

```java
private static final String API_KEY = "<insert api key>";
```

Replace the string `<insert api key>` with your API key.  If you don't have an API key, you can get one by following the steps [here](https://github.com/volarvideo/cms-client-sdk/wiki/Creating-api-credentials).  You can also use your Volar account credentials to instantiate the `VVCMSAPI` class.  Here's an example that's commented out in `MediaListActivity`.

```java
api = new VVCMSAPI(DOMAIN, "john@test.com", "password");
```

## Included Libraries

### UniversalImageLoader

	Copyright (c) 2011-2012, Sergey Tarasevich
	URL: https://github.com/nostra13/Android-Universal-Image-Loader