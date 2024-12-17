FROM jenkins/jenkins:2.485-jdk21

ARG maven_version=3.9.9

ENV MAVEN_HOME=/opt/maven
ENV M2_HOME=/opt/maven
ENV PATH=/opt/java/openjdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:${M2_HOME}/bin

USER root

RUN apt-get update && apt-get install wget -y && apt-get clean
# Download latest Maven manually
RUN wget https://dlcdn.apache.org/maven/maven-3/"$maven_version"/binaries/apache-maven-"$maven_version"-bin.tar.gz -P /tmp
# Extract Maven Archive
RUN tar xf /tmp/apache-maven-"$maven_version"-bin.tar.gz -C /opt
# Create a Symbolic Link
RUN ln -s /opt/apache-maven-"$maven_version" /opt/maven

# important to switch back to Jenkins user. Otherwise there will be permissions issues (if everything will be created by root then Jenkins won't be able to work with directories since is uses it's own user - jenkins)
USER jenkins



# docker build --no-cache -t jenkins_image .
# docker-compose up -d / docker-compose down jenkins
# docker run -d --rm --name jenkins -p 8080:8080 -p 50000:50000 -e Q_TEST_KEY -e MAILINATOR_KEY -e BROWSERSTACK_USER -e BROWSERSTACK_KEY -v /c/Users/Admin/Documents/Pharmacann/docker/jenkins:/var/jenkins_home jenkins_image
# Jenkins local nazar/1234