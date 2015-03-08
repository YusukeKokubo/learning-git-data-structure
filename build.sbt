enablePlugins(ScalaJSPlugin)

name := "Scala.js Tutorial"

scalaVersion := "2.11.5"

scalaJSStage in Global := FastOptStage

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"
libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0"
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.2.6"

skip in packageJSDependencies := false

jsDependencies += RuntimeDOM


testFrameworks += new TestFramework("utest.runner.Framework")