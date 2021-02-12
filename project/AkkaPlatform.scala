object AkkaPlatform {

  val AkkaVersion = "2.6.12"
  val AkkaHttpVersion = "10.2.3"
  val AkkaManagementVersion = "1.0.9"
  val AkkaPersistenceJdbcVersion = "5.0.0"
  val AkkaPersistenceCassandraVersion = "1.0.4"
  val AlpakkaKafkaVersion = "2.0.6"
  val AkkaProjectionVersion = "1.1.0"

  object artifacts {
    // 1. Basic dependencies for a clustered application
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
    val akkaCluster = "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion
    val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion
    val akkaTestKit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
    val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test

    // Akka Management powers Health Checks and Akka Cluster Bootstrapping
    val akkaMng = "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
    val akkaHttpJson = "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
    val akkaMngClusterHttp = "com.lightbend.akka.management" %% "akka-management-cluster-http" % AkkaManagementVersion
    val akkaMngClusterBootstrap =
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % AkkaManagementVersion
    val akkaDiscoveryKubeApi = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % AkkaManagementVersion
    val akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % AkkaVersion

    // Common dependencies for logging and testing
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion
    val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
    val scalaTest = "org.scalatest" %% "scalatest" % "3.1.2" % Test

    // 2. Using gRPC and/or protobuf
    val akkaHttp2 = "com.typesafe.akka" %% "akka-http2-support" % AkkaHttpVersion

    // 3. Using Akka Persistence
    val akkaPersistence = "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion
    val akkaJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
    val akkaPersistenceJdbc = "com.lightbend.akka" %% "akka-persistence-jdbc" % AkkaPersistenceJdbcVersion
    val akkaPersistenceCassandra = "com.lightbend.akka" %% "akka-persistence-cassandra" % AkkaPersistenceJdbcVersion
    val akkaPersistenceTestKit = "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test

    // 4. Querying or projecting data from Akka Persistence
    val akkaPersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion
    val akkaProjectionEventSourced = "com.lightbend.akka" %% "akka-projection-eventsourced" % AkkaProjectionVersion
    val akkaProjectionJdbc = "com.lightbend.akka" %% "akka-projection-jdbc" % AkkaProjectionVersion
    val akkaProjectionCassandra = "com.lightbend.akka" %% "akka-projection-cassandra" % AkkaProjectionVersion
    val akkaProjectionSlick = "com.lightbend.akka" %% "akka-projection-slick" % AkkaProjectionVersion
    val akkaProjectionKafka = "com.lightbend.akka" %% "akka-projection-kafka" % AkkaProjectionVersion
    val akkaStreamKafka = "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion
    val akkaProjectionTestkit = "com.lightbend.akka" %% "akka-projection-testkit" % AkkaProjectionVersion % Test

  }
  import artifacts._

  /**
   * Dependencies to build a minimal Akka Cluster
   */
  val clusterSharding = Seq(akkaCluster, akkaClusterSharding, akkaJackson, logback, akkaSlf4j)

  /**
   * Akka Management
   */
  val clusterManagement = Seq(akkaMng, akkaMngClusterHttp, akkaMngClusterBootstrap, akkaDiscovery, akkaDiscoveryKubeApi)

  /**
   * Dependencies to build a minimal Akka Http Server
   */
  val httpServer = Seq(akkaHttp, akkaHttpJson, akkaHttp2, logback, akkaSlf4j)

  //--------------------------------------------------------------------------------
  // Akka Persistence
  private val minimalPersistence = Seq(akkaPersistence, akkaJackson, akkaPersistenceTestKit)

  /**
   * base dependencies for a write-side using JDBC
   */
  val persistenceJdbc = minimalPersistence :+ akkaPersistenceJdbc

  /**
   * base dependencies for a write-side using Cassandra
   */
  val persistenceCassandra = minimalPersistence :+ akkaPersistenceCassandra
  //--------------------------------------------------------------------------------

  //--------------------------------------------------------------------------------
  // Akka Projections
  private val minimalProjection =
    Seq(akkaPersistenceQuery, akkaProjectionEventSourced, akkaProjectionTestkit)

  /**
   * base dependencies for a read-side projection using JDBC
   */
  val projectionJdbc = minimalProjection :+ akkaProjectionJdbc

  /**
   * base dependencies for a read-side projection using Slick
   */
  val projectionSlick = minimalProjection :+ akkaProjectionSlick

  /**
   * base dependencies for a read-side projection using Cassandra
   */
  val projectionCassandra = minimalProjection :+ akkaProjectionCassandra

  /**
   * base dependencies for a read-side projection using Kafka
   */
  val projecitonKafka = minimalProjection :+ akkaProjectionKafka
  //--------------------------------------------------------------------------------
}
