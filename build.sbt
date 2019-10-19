name := "sgit"

version := "0.1"

scalaVersion := "2.13.1"


lazy val root = (project in file("."))
  .settings(
    name := "sgit",
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2", 
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.9",
    libraryDependencies += "commons-io" % "commons-io" % "2.5",
    parallelExecution in Test := false
  )

import sbtassembly.AssemblyPlugin.defaultUniversalScript

assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultUniversalScript(shebang = false)))
assemblyJarName in assembly := s"${name.value}"


