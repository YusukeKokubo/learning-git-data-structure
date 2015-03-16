enablePlugins(ScalaJSPlugin)

name := "Understanding Git data structure with GitHub API and Scala.js"

scalaVersion := "2.11.5"

scalaJSStage in Global := FastOptStage

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.2.6"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.4.5"
libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.2.7"

skip in packageJSDependencies := false

jsDependencies += RuntimeDOM
jsDependencies += "org.webjars" % "jquery" % "1.11.1" / "jquery.js"
jsDependencies += "org.webjars" % "bootstrap" % "3.3.2-2" / "bootstrap.js" dependsOn "jquery.js"


testFrameworks += new TestFramework("utest.runner.Framework")