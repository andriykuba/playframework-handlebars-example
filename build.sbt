name := """handlebars"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Eclipse IDE support
// Compile the project before generating Eclipse files,
// so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)
// Java project. Don't expect Scala IDE
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
// Use .class files instead of generated .scala files for views and routes
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)


// Add the handlebars library
libraryDependencies += "com.github.jknack" % "handlebars" % "4.0.4"
libraryDependencies += "com.github.jknack" % "handlebars-guava-cache" % "4.0.4"

// Add the Mockito for tests
libraryDependencies += "org.mockito" % "mockito-core" % "2.0.44-beta"

pipelineStages := Seq(digest)

// Copy handlebars templates to the production
mappings in Universal ++=
  (baseDirectory.value / "templates" * "*" get) map
    (x => x -> ("templates/" + x.getName))
