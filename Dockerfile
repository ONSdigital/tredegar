from java:8

# Install git and maven

RUN \
  apt-get update && \
  apt-get install -y git maven 

# Consul agent - /usr/local/bin

ADD https://dl.bintray.com/mitchellh/consul/0.5.2_linux_amd64.zip /tmp/0.5.2_linux_amd64.zip
WORKDIR /usr/local/bin
RUN unzip /tmp/0.5.2_linux_amd64.zip
WORKDIR /etc/consul.d
RUN echo '{"service": {"name": "tredegar", "tags": ["blue"], "port": 8080, "check": {"script": "curl http://localhost:8080 >/dev/null 2>&1", "interval": "10s"}}}' > tredegar.json

# Check out code from Github

WORKDIR /usr/src
RUN git clone https://github.com/ONSdigital/tredegar.git
WORKDIR tredegar
RUN git checkout develop

#Generate taxonomy
RUN mvn clean compile dependency:copy-dependencies

# Now build the JAR:
RUN mvn process-resources

# Expose port
EXPOSE 8080

# Build the entry point script

ENV ZEBEDEE_URL http://zebedee:8080

# Restolino configuration
ENV RESTOLINO_STATIC="src/main/resources/files"
ENV RESTOLINO_CLASSES="target/classes"
ENV PACKAGE_PREFIX=com.github.onsdigital

# Mongodb
ENV MONGO_USER=ons
ENV MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C

RUN echo "#!/bin/bash" >> container.sh
# Disabled for now: RUN echo "consul agent -data-dir /tmp/consul -config-dir /etc/consul.d -join=dockerhost &" > container.sh
RUN echo "java $JAVA_OPTS \
           -Drestolino.username=$USERNAME \
           -Drestolino.password=$PASSWORD \
           -Drestolino.realm=$REALM \
           -Drestolino.files=$RESTOLINO_STATIC \
           -Drestolino.classes=$RESTOLINO_CLASSES \
           -Drestolino.packageprefix=$PACKAGE_PREFIX \
           -Dmongo.user=$MONGO_USER \
           -Dmongo.password=$MONGO_PASSWORD \
           -cp "target/dependency/*" \
           com.github.davidcarboni.restolino.Main" >> container.sh
RUN chmod u+x container.sh
RUN cat container.sh

ENTRYPOINT ["./container.sh"]
