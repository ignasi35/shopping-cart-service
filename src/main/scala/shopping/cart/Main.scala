package shopping.cart

import akka.application.internal.AkkaClusterApplication

object Main extends AkkaClusterApplication {
  override val applicationName: String = "ShoppingCartService"
}
