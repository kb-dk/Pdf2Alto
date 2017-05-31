#!/bin/sh

##Converts single page PDFs to JP2000

find . -iname "*.pdf"  | while read filename;
 do
  filename1=$(basename "$filename" .pdf)
  echo $filename;
  filenameJp2=$filename1'.jp2'
  echo $filenameJp2;
  pdftoppm -r 400  $filename xxx
  convert -density 400 -quality 0  xxx-1.ppm  /media/teg/1200GB_SSD/alto/image/$filenameJp2

 done





