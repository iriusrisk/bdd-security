#!/bin/sh
export GRADLE_OPTS=-Xmx512m
./gradlew testStory -Dargs="-story $1"
