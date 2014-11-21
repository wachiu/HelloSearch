cd bin && jar -cvfm ../executable/app.jar manifest/crawler_manifest.txt IRUtilities/*.class project/*.class org/json/*.class
cd ../executable && java -jar app.jar search "check"
#cd ../executable && java -jar app.jar index
cd ..
chmod 777 executable/*.db
chmod 777 executable/*.lg
