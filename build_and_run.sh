#!/usr/bin/env bash
echo -n "Building apk..."
ant instrument -q || exit;

echo ""
echo -n "Installing apk..."
ant installi -q || exit;

echo ""
echo "Starting app..."
adb shell am start -a android.intent.action.MAIN -n com.volarvideo.demoapp/.BroadcastsActivity || exit;
