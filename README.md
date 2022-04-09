#Million 4 Ukraine Project

An application intended to collect donations and reward the effort by letting the users post a picture of their choice.

## Requirements

- Angular CLI installed
- Java 11 installed

## How to build

Execute the following command from the prompt:

``mvnw clean install``

## How to run

You will need to run the spring-boot rest service and the angular application.

To run the spring-boot rest service go to the `m4u-rest` directory and run:

``mvn spring-boot:run``

To run in debug mode:
``mvn spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787 -Dserver.port=8080"``

To run the angular application go to the `m4u-ui` directory and run:

``ng serve``

## Other useful info

To view the embedded hsqldb data you can use the built-in viewer. Launch it with this command:

``"%JAVA_HOME%\BIN\java" -cp C:\Users\[username]\.m2\repository\org\hsqldb\hsqldb\2.5.2\hsqldb-2.5.2.jar  org.hsqldb.util.DatabaseManagerSwing``

Make sure you chage the paths accordingly