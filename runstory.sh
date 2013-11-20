#!/bin/sh
export ANT_OPTS=-Xmx500m
ant test -Dargs="-story $1"
