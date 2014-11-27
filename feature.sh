#!/bin/bash
if [ -z "$1" ]
then
    echo "Please provide a branch name."
else
    echo git push -f featuretredegar $1:master
    git push -f featuretredegar $1:master
fi
