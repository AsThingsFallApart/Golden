# Golden
Query a MySQL database using a Java GUI

INSTALLING NECESSARY DEPENDENCIES:
--------------------------------------------------

Golden depends on 'Connector/J' by Oracle MySQL Project Engineering Team.

The Connector/J .jar file can be downloaded at https://dev.mysql.com/downloads/connector/j/.

Adding Connector/J to the java project:

1. Create an environment variable called 'CLASSPATH' assigned to wherever the Connector/J .jar file is. 

	e.g. in BASH:
		"export CLASSPATH="/[path]/mysql-connector-java-[version].jar"

2. If using an IDE, add the path to the Connector/J .jar file to the build path.

	e.g. in Visual Studio Code:
		in the '.vscode' directory:
			in the 'settings.json' file:
				"{
					"java.project.referencedLibraries": [
						"/[path]/mysql-connector-java-[version].jar"
					]
				}"
