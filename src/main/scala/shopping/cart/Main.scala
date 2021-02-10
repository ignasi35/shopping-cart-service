package shopping.cart

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.application.internal.AkkaClusterApplication
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse

object Main extends AkkaClusterApplication {

  override val applicationName: String = "ShoppingCartService"

  /**
   * User main function.
   */
  override def init(args: Array[String])(implicit system: ActorSystem[Nothing]): HttpRequest => Future[HttpResponse] = {

    // user gRPC service
    val service = new ShoppingCartServiceImpl(system)
    ServiceHandler.concatOrNotFound(
      proto.ShoppingCartServiceHandler.partial(service),
      // ServerReflection enabled to support grpcurl without import-path and proto parameters
      ServerReflection.partial(List(proto.ShoppingCartService)))
  }
}
