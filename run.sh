if [ "$1" == "compile" ]
then
	cd bin && jar -cvfm ../executable/app.jar manifest/crawler_manifest.txt IRUtilities/*.class project/*.class org/json/*.class
	cd ..
elif [ "$1" == "index" ]
then
	cd executable && java -jar app.jar index
        cd ..
        chmod 777 executable/*.db
        chmod 777 executable/*.lg
elif [ "$1" == "search" ]
then
	cd executable && java -jar app.jar search "$2"
	cd ..
elif [ "$1" == "rmdb" ]
then
	rm executable/*.db executable/*.lg
fi

