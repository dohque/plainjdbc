
name := "plainjdbc"

organization := "com.dohque"

version := "0.1"

scalaVersion := "2.11.8"

javacOptions := Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "org.specs2"                   %% "specs2-core"             % "3.8.5"    % Test,
  "org.specs2"                   %% "specs2-junit"            % "3.8.5"    % Test,
  "org.specs2"                   %% "specs2-mock"             % "3.8.5"    % Test,
  "com.h2database"               %  "h2"                      % "1.4.192"  % Test
)
