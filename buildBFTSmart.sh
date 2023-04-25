rm lib/BFT-SMaRt.jar
rm lib/bcutil-jdk15on-1.69.jar
rm lib/logback-core-1.2.5.jar
rm lib/commons-codec-1.15.jar
rm lib/netty-all-4.1.67.Final.jar
rm lib/bcpkix-jdk15on-1.69.jar
rm lib/slf4j-api-1.7.32.jar
rm lib/bcprov-jdk15on-1.69.jar
rm lib/logback-classic-1.2.5.jar
rm lib/opencsv-5.0.jar
rm lib/commons-text-1.7.jar
rm lib/commons-lang3-3.9.jar
rm lib/commons-beanutils-1.9.4.jar
rm lib/commons-collections4-4.4.jar
rm lib/commons-logging-1.2.jar
rm lib/commons-collections-3.2.2.jar
./gradlew installDist
cp build/install/BFT-SMART/lib/* lib/
cp ./build/install/BFT-SMART/smartrun.sh runscripts


