#!/bin/bash
cur_dir=`dirname $0` ;
cd $cur_dir ;
cp -rf workspace/Work/pretups/* workspace/Final/pretups/
rm -rf workspace/Final/pretups/src/main/java/com/btsl  workspace/Final/pretups/src/main/java/com/inet workspace/Final/pretups/src/main/java/EXTGW
cp -rf workspace/Work/pretups/src/main/java/com/selftopup workspace/Final/pretups/src/main/java/com/
cp -rf workspace/Work/pretups/project_repo_lib workspace/Final/pretups/
cp -rf workspace/Work/pretups/pom_SelfTopUp.xml workspace/Final/pretups/
mv workspace/Final/pretups/pom_SelfTopUp.xml workspace/Final/pretups/pom.xml


