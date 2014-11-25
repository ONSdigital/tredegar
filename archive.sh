#!/bin/bash
if [ -z "$1" ]
then
    echo "Please provide a branch name."
else
    echo Archiving branch $1
    git tag archive/$1 $1
    git branch -d $1
    git push origin :$1
    git push --tags
fi
