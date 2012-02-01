#!/bin/sh
DIR=`dirname $0`
SBT_VERSION=0.11.0
if [ ! -e $DIR/sbt-launch.jar ]; then
  (cd $DIR; wget http://typesafe.artifactoryonline.com/typesafe/ivy-releases/org.scala-tools.sbt/sbt-launch/$SBT_VERSION/sbt-launch.jar)
fi
java -XX:ReservedCodeCacheSize=1024m -XX:MaxPermSize=512m -Xmx1024m -jar $DIR/sbt-launch.jar $@
