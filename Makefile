processdata:
	javac -cp JLink.jar src/*.java
	java -cp ./src;../ Analysis

runmodel:
	java -classpath "./src;c:\program files\wolfram research\mathematica\13.3\SystemFiles\Links\JLink\JLink.jar" Modelling -linkmode launch -linkname "c:\program files\wolfram research\mathematica\13.3\mathkernel.exe"

clean:
	rm -f src/*.class