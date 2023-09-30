FROM amazoncorretto:19
MAINTAINER lm "mueller.lutz@gmail.com"

ADD *.jar /app.jar


CMD chmod a+rwx /app.jar && mkdir -p /mnt/media &&  mkdir /filescanner && chmod a+rwx /filescanner



EXPOSE 8002

VOLUME ["/filescanner"]
VOLUME ["/mnt/media"]

ENTRYPOINT ["java","-jar","-XX:InitialRAMPercentage=70", "-XX:MaxRAMPercentage=70" ,"-XX:MinRAMPercentage=50","-Dspring.data.mongodb.host=mongo","-Dfilescanner.path.contentroot=file:///mnt/media","-Dfilescanner.path.home=/filescanner","/app.jar"]