#!/bin/sh
export ANT_OPTS=-Xmx500m
ant test -Dargs="-c"
