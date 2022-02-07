default:
	cd khelli-bot\java && mvn clean install && cd ..\..\ && cd bryan-bot\java && mvn clean install && cd ..\..\ && .\run.bat

run:
	java -jar ./game-runner-jar-with-dependencies.jar
