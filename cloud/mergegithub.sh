#!/bin/bash
#git checkout -b branch_usage_20130624 ETBGit/branch_usage_20130624
BRANCH=branch_usage_20130624
git checkout $BRANCH
if [ $? -ne 0 ];then
    echo "Failed to checkout $BRANCH"
    exit 1
fi
git pull ETBGit $BRANCH
if [ $? -ne 0 ];then
    echo "Failed to pull $BRANCH from ETBGit"
    exit 1
fi
git merge Github/usage_dev_branch
if [ $? -ne 0 ];then
    echo "Failed to merge from Github"
    exit 1
fi
mvn -f clou/pom.xml clean install lombok:delombok -Pcmt-hattara-dev

