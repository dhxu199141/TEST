
D:\"Program Files"\Java\jdk1.5.0_09\bin\javac *.java

D:\"Program Files"\Java\jdk1.5.0_09\bin\rmic rmiShepherdServerUtilImpl

start  D:\"Program Files"\Java\jdk1.5.0_09\bin\rmiregistry

java -Djava.security.policy=RmiHelloServer.policy rmiShepherdServer

pause

