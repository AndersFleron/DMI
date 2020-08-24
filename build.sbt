

lazy val root = (project in file("."))
  .settings(
    name := "DMI",
    version := "0.1",
    scalaVersion := "2.12.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test",
    libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC11",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.24.0",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "0.9.5",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.24.0",
    libraryDependencies += "com.softwaremill.sttp.client" %% "okhttp-backend" % "2.0.0-RC11"
    )





