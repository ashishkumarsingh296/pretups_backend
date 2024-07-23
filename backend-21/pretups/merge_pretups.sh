#!/bin/bash
cur_dir=`dirname $0` ;
cd $cur_dir ;
rm -rf workspace/Final/pretups/src/main/java/com/btsl/*
cp -rf workspace/Work/pretups/* workspace/Final/pretups/
