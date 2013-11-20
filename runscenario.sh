#!/bin/sh
export ANT_OPTS=-Xmx500m
ant test -Dargs="-id $1"
