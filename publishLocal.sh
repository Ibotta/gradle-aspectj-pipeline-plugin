#!/bin/bash

if [[ -n $TRAVIS_BUILD_NUMBER ]]; then
  echo Updating plugin version with build number: $TRAVIS_BUILD_NUMBER
  sed -i -e 's/BUILD_NUMBER = ""/BUILD_NUMBER = ".'$TRAVIS_BUILD_NUMBER'"/g' buildSrc/src/main/kotlin/Dependencies.kt
  rm buildSrc/src/main/kotlin/Dependencies.kt-e
fi
./gradlew publishToMavenLocal -DpublishMode=true