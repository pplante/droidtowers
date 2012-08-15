#!/bin/bash

INPUT=$1
OUTPUT_NAME=$2
OUTPUT='test/'

rm $OUTPUT/*
ffmpeg -i $INPUT -an -f image2 -r 4 -s 64x27 $OUTPUT/image_%05d.jpg
TexturePacker --algorithm MaxRects --padding 0 --disable-rotation --format libgdx $OUTPUT --data $OUTPUT_NAME.txt --sheet $OUTPUT_NAME.png
