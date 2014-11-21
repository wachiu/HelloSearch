cd bin && jar -cvfm ../executable/app.jar manifest/crawler_manifest.txt IRUtilities/*.class project/*.class org/json/*.class
cd ../executable && java -jar app.jar search "hkust test"
#cd ../executable && java -jar app.jar index
cd ..
