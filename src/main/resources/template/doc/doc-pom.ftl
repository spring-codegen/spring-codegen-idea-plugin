<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${basePkg}</groupId>
    <artifactId>${projectName}</artifactId>
    <version>0.0.1</version>
    <name>${projectName}</name>
    <description>${projectName}</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>net.takela</groupId>
            <artifactId>common-web</artifactId>
            <version>1.0.1</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.ly.smart-doc</groupId>
                <artifactId>smart-doc-maven-plugin</artifactId>
                <version>3.0.2</version>
                <configuration>
                    <configFile>./doc-config.json</configFile>
                    <includes>
                        <include>net.takela:common-web</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
