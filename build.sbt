lazy val root = project
  .in(file("."))
  .aggregate(core.jvm, core.js, scalaz, argonaut, cats.jvm, cats.js)
  .settings(publishArtifact := false)
  .settings(publishSettings: _*)
  .settings(mimaSettings: _*)

lazy val core = crossProject.crossType(CrossType.Pure)
  .settings(commonSettings: _*)
  .settings(name := "oneand-core")
lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val cats = crossProject.crossType(CrossType.Pure)
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings: _*)
  .settings(name := "oneand-cats")
  .settings(
    // scalaz
    libraryDependencies += "org.spire-math" %%% "cats-macros" % "0.3.0",
    libraryDependencies += "org.spire-math" %%% "cats-core" % "0.3.0",
    libraryDependencies += "org.spire-math" %%% "cats-laws" % "0.3.0" % "test"
  )
lazy val catsJVM = cats.jvm
lazy val catsJS = cats.js

lazy val scalaz = project
  .dependsOn(core.jvm % "compile->compile;test->test")
  .settings(commonSettings: _*)
  .settings(name := "oneand-scalaz")
  .settings(
    // scalaz
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.5",
    libraryDependencies += "org.scalaz" %% "scalaz-scalacheck-binding" % "7.1.5" % "test"
  )

lazy val argonaut = project
  .dependsOn(scalaz)
  .settings(commonSettings: _*)
  .settings(name := "oneand-argonaut")
  .settings(
    libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"
  )

def specs2 = Seq(
  libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.6.5" % "test"),
  libraryDependencies ++= Seq("org.specs2" %% "specs2-scalacheck" % "3.6.5" % "test"),
  scalacOptions in Test ++= Seq("-Yrangepos")
)

lazy val currentVersion = "0.1.1-SNAPSHOT"
lazy val previousVersion = "0.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  version := currentVersion,
  scalacOptions ++= Seq("-feature","-language:higherKinds"),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
) ++ publishSettings ++ specs2 ++ mimaSettings

lazy val publishSettings = Seq(
  organization := "net.arya",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else                  Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomExtra := (
    <scm>
      <url>git@github.com:refried/oneand.git</url>
      <connection>scm:git:git@github.com:refried/oneand.git</connection>
    </scm>
      <developers>
        <developer>
          <id>refried</id>
          <name>Arya Irani</name>
          <url>https://github.com/refried</url>
        </developer>
      </developers>),
  licenses := Seq("MIT-style" -> url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/refried/oneand"))
)


import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.tools.mima.plugin.MimaKeys._
lazy val mimaSettings = mimaDefaultSettings ++ Seq(
  previousArtifact <<= (version, organization, scalaBinaryVersion, moduleName)(
    (ver, org, binVer, mod) =>
      Some(org % s"${mod}_${binVer}" % previousVersion)
  ),
  binaryIssueFilters ++= {
    import com.typesafe.tools.mima.core._
    import com.typesafe.tools.mima.core.ProblemFilters._
    Seq(
    )
  }
)
