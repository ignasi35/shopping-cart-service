package shopping.cart

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.application.internal.HttpServer
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import shopping.cart.repository.ItemPopularityRepositoryImpl
import shopping.cart.repository.ScalikeJdbcSetup

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem[Nothing](Behaviors.empty[Nothing], "ShoppingCartService")

    AkkaManagement(system).start()
    ClusterBootstrap(system).start()

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
    val grpcRoute =
      ServiceHandler.concatOrNotFound(
        proto.ShoppingCartServiceHandler.partial(service),
        // ServerReflection enabled to support grpcurl without import-path and proto parameters
        ServerReflection.partial(List(proto.ShoppingCartService)))

    system.log.debug("Starting http server")
    HttpServer.start(system, grpcRoute)
  }

}
