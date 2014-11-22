if [ "$1" == "compile" ]
then
	cd bin && javac -cp ../executable/jsoup-1.8.1.jar:../executable/jdbm-1.0.jar:. ../IRUtilities/*.java ../project/*.java ../org/json/*.java -d ./
	jar -cvfm ../executable/app.jar manifest/crawler_manifest.txt IRUtilities/*.class project/*.class org/json/*.class
	cd ..
        chmod 777 executable/*.db
        chmod 777 executable/*.lg
elif [ "$1" == "index" ]
then
	rm executable/*.db executable/*.lg
	cd executable && java -jar app.jar index
        cd ..
        chmod 777 executable/*.db
        chmod 777 executable/*.lg
elif [ "$1" == "search" ]
then
	cd executable && java -jar app.jar search "$2"
	cd ..
        chmod 777 executable/*.db
        chmod 777 executable/*.lg
elif [ "$1" == "stem" ]
then
        cd executable && java -jar app.jar stem "$2"
        cd ..
        chmod 777 executable/*.db
        chmod 777 executable/*.lg
fi

