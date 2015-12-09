lazy val root = project.in(file(".")).aggregate(scalaz).settings(publishArtifact := false)

lazy val scalaz = project
  .settings(commonSettings: _*)
  .settings(name := "oneand-scalaz")
  .settings(
    // scalaz
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.5",
    libraryDependencies += "org.scalaz" %% "scalaz-scalacheck-binding" % "7.1.5" % "test",
    // specs2
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.6.5" % "test"),
    libraryDependencies ++= Seq("org.specs2" %% "specs2-scalacheck" % "3.6.5" % "test"),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  version := "0.1",
  organization := "net.arya",
  scalacOptions ++= Seq("-feature","-language:higherKinds"),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
) ++ publishSettings

lazy val publishSettings = Seq(
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
