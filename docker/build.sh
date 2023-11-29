#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "No version set"
    exit -1
fi

VERSION=$1
IMAGE_NAME=$2

echo "begin to build backend $VERSION"

BASE_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && cd ../ && pwd )
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)


if [ -d $SCRIPT_DIR/app ];then
   rm -fr $SCRIPT_DIR/app
fi

#
# sdk use java 17.0.7-tem
cd $BASE_DIR && sbt universal:packageBin

if [ $? -eq 0 ];then
   echo "build backend success"
else
   echo "build backend failure"
   exit 1
fi

cp $BASE_DIR/target/universal/app.zip $SCRIPT_DIR/app.zip

cd $SCRIPT_DIR && unzip app.zip

# This is for MAC M1
export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker build . -t=$IMAGE_NAME:$VERSION && docker push $IMAGE_NAME:$VERSION

if [ $? -eq 0 ];then
   echo "build backend image successfully!"
fi