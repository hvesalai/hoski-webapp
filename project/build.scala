import sbt._
import Keys._
import com.github.siasia._
import WebPlugin.webSettings

object BuildSettings {
   val buildSettings = Defaults.defaultSettings ++ Seq (
     organization        := "fi.hoski",
     version             := "1.0-SNAPSHOT",
     scalaVersion        := "2.9.0-1"
  )
}

object Dependencies {
  var servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"

  // test framework
  var junitInterface = "com.novocode" % "junit-interface" % "0.7" % "test->default"
  var servletTester = "org.eclipse.jetty" % "test-jetty-servlet" % "7.5.0.v20110901" % "test"

  // embedded jetty
  var jetty = "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

  // logging
  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.6.1" % "compile"
  val slf4jBinding = "ch.qos.logback" % "logback-classic" % "0.9.28" % "runtime"

}

object HoskiBuild extends Build {
  import Dependencies._
  import BuildSettings._

  val deps = Seq (
//    slf4jApi,
//    slf4jBinding,
    junitInterface,
    servletTester,
    servletApi,
    jetty
  )

  lazy val hoskiWebapp = Project (
    "hoski-webapp",
    file ("."),
    settings = buildSettings
  )
  .settings(libraryDependencies ++= deps /*,
            javacOptions ++= Seq("-Xlint:unchecked")*/)
  .settings(webSettings :_*)

}
