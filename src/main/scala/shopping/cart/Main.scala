package shopping.cart

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.application.internal.AkkaClusterApplication
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import shopping.cart.repository.ItemPopularityRepositoryImpl
import shopping.cart.repository.ScalikeJdbcSetup

object Main extends AkkaClusterApplication {

  override val applicationName: String = "ShoppingCartService"

  /**
   * User main function.
   */
  override def init(args: Array[String])(implicit system: ActorSystem[Nothing]): HttpRequest => Future[HttpResponse] = {

    // user DB tech choice bootstrap
    ScalikeJdbcSetup.init(system)

    // user entity
    ShoppingCart.init(system)

    // user read-side repository
    val repository = new ItemPopularityRepositoryImpl

    // user read-side projection
    ItemPopularityProjection.init(system, repository)

    // user gRPC service
    val service = new ShoppingCartServiceImpl(system, repository)
    ServiceHandler.concatOrNotFound(
      proto.ShoppingCartServiceHandler.partial(service),
      // ServerReflection enabled to support grpcurl without import-path and proto parameters
      ServerReflection.partial(List(proto.ShoppingCartService)))
  }
}
