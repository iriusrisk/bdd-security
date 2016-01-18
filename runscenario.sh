#!/bin/sh
export GRADLE_OPTS=-Xmx512m
./gradlew testStory -Dargs="-id $1"
