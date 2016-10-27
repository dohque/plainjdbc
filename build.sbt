
name := "plainjdbc"

organization := "com.dohque"

version := "0.1-SNAPSHOT"

description := "Small Scala library to execute generated sql statements over plain jdbc"

homepage := Some(url(s"https://github.com/dohque/plainjdbc#readme"))

scalaVersion := "2.11.8"

javacOptions := Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "org.specs2"                   %% "specs2-core"             % "3.8.5"    % Test,
  "org.specs2"                   %% "specs2-junit"            % "3.8.5"    % Test,
  "org.specs2"                   %% "specs2-mock"             % "3.8.5"    % Test,
  "com.h2database"               %  "h2"                      % "1.4.192"  % Test
)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := Some("Artifactory Realm" at "https://oss.jfrog.org/artifactory/oss-snapshot-local")

pomExtra := (
    <scm>
      <url>git@github.com:dohque/plainjdbc.git</url>
      <connection>scm:git:git@github.com:dohque/plainjdbc.git</connection>
    </scm>
    <developers>
      <developer>
        <id>dohque</id>
        <name>Ruslan Pilin</name>
        <url>https://github.com/dohque</url>
      </developer>
    </developers>
  )
