package shopping.cart

import java.util.concurrent.TimeoutException

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.grpc.GrpcServiceException
import akka.util.Timeout
import io.grpc.Status
import org.slf4j.LoggerFactory

class ShoppingCartServiceImpl(system: ActorSystem[_]) extends proto.ShoppingCartService {

  import system.executionContext

  private val logger = LoggerFactory.getLogger(getClass)

  implicit private val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("shopping-cart-service.ask-timeout"))

  //  private val sharding = ClusterSharding(system)

  //  private val blockingJdbcExecutor: ExecutionContext =
  //    system.dispatchers.lookup(DispatcherSelector.fromConfig("akka.projection.jdbc.blocking-jdbc-dispatcher"))

  override def addItem(in: proto.AddItemRequest): Future[proto.Cart] = {
    logger.info("addItem {} to cart {}", in.itemId, in.cartId)
    Future.successful(proto.Cart(items = List(proto.Item(in.itemId, in.quantity))))
  }

  override def updateItem(in: proto.UpdateItemRequest): Future[proto.Cart] = ???

  override def checkout(in: proto.CheckoutRequest): Future[proto.Cart] = ???

  override def getCart(in: proto.GetCartRequest): Future[proto.Cart] = ???

  private def toProtoCart(cart: ShoppingCart.Summary): proto.Cart = {
    proto.Cart(
      cart.items.iterator.map { case (itemId, quantity) =>
        proto.Item(itemId, quantity)
      }.toSeq,
      cart.checkedOut)
  }

  private def convertError[T](response: Future[T]): Future[T] = {
    response.recoverWith {
      case _: TimeoutException =>
        Future.failed(new GrpcServiceException(Status.UNAVAILABLE.withDescription("Operation timed out")))
      case exc =>
        Future.failed(new GrpcServiceException(Status.INVALID_ARGUMENT.withDescription(exc.getMessage)))
    }
  }

  override def getItemPopularity(in: proto.GetItemPopularityRequest): Future[proto.GetItemPopularityResponse] = ???
}
