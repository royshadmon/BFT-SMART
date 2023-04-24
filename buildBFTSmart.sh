rm lib/BFT-SMaRt.jar
rm lib/bcutil-jdk15on-1.69.jar
rm lib/logback-core-1.2.5.jar
rm lib/commons-codec-1.15.jar
rm lib/netty-all-4.1.67.Final.jar
rm lib/bcpkix-jdk15on-1.69.jar
rm lib/slf4j-api-1.7.32.jar
rm lib/bcprov-jdk15on-1.69.jar
rm lib/logback-classic-1.2.5.jar
./gradlew installDist
cp build/install/BFT-SMART/lib/* lib/
cp ./build/install/BFT-SMART/smartrun.sh runscripts


