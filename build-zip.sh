#!/bin/sh
home=$(pwd)
rm -rf tmp
echo "Build java distribution zip file using gradle"
./gradlew clean buildit jar
echo "Distribution zip built successfully"

mkdir tmp
cp ./build/distributions/bdd-security.zip tmp/bdd-security.zip
cd tmp && unzip bdd-security.zip && cd -
rm tmp/bdd-security.zip

cp config.xml log4j.properties tmp/
echo $pwd
cp -R zap/ tmp/
cp -R --parent src/main/resources/ tmp/

cd tmp
zip -r securityTest.zip .

echo "Zip artifact for Security Test is created." 
