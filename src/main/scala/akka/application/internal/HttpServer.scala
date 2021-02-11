package akka.application.internal

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import scala.concurrent.duration._

object HttpServer {

  def start(system: ActorSystem[_], httpF: HttpRequest => Future[HttpResponse]): Unit = {
    implicit val sys: ActorSystem[_] = system
    implicit val ec: ExecutionContext =
      system.executionContext

    val interface = system.settings.config.getString("akka.http.interface")
    val port = system.settings.config.getInt("akka.http.port")

    val bound =
      Http().newServerAt(interface, port).bind(httpF).map(_.addToCoordinatedShutdown(3.seconds))

    bound.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Akka Http server running at {}:{}", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind endpoint, terminating system", ex)
        system.terminate()
    }
  }

}
