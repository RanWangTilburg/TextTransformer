// The Typesafe repository
resolvers += Resolver.typesafeRepo("releases")
  resolvers += "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"

  addSbtPlugin("org.spark-packages" % "sbt-spark-package" % "0.2.5")
addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.9.4")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
