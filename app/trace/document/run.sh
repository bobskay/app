cd ~/app/app/trace
mvn install -DskipTests=true
nohup java -jar ./target/trace-1.0.jar &

