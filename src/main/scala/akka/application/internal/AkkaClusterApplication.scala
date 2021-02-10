package akka.application.internal

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

trait AkkaClusterApplication {

  // <1> user must define the name of the application, used as actor system name
  val applicationName: String

  // <2> traditional main function is final
  final def main(args: Array[String]): Unit = {

    // top-level guardian behavior
    val guardianBehavior =
      Behaviors.setup[Nothing] { context =>
        val system = context.system

        context.log.debug("Initializing application")
        AkkaManagement(system).start()
        ClusterBootstrap(system).start()

        // call user init function (following Akka init tradition)
        // - takes traditional args plus an implicit typed actor system (the guardian)
        // - returns the user routes
        val userRoute = init(args)(system)

        context.log.debug("Starting http server")
        val interface = system.settings.config.getString("akka.http.interface")
        val port = system.settings.config.getInt("akka.http.port")
        HttpServer.start(interface, port, system, userRoute)

        Behaviors.empty[Nothing]
      }

    ActorSystem[Nothing](guardianBehavior, applicationName)
  }

  /**
   * User main function.
   */
  def init(args: Array[String])(implicit system: ActorSystem[Nothing]): HttpRequest => Future[HttpResponse] =
    path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Your Akka Application is running</h1>"))
      }
    }

}
