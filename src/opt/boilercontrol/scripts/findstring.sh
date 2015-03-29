#!/bin/sh
find . -type f -exec grep -i "$1" {} \; -print
