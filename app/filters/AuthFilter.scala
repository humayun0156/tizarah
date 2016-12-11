package filters

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import utils.EncryptUtil

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */
class AuthFilter @Inject()(implicit override val mat: Materializer,
                           exec: ExecutionContext) extends Filter {

  def apply(next: (RequestHeader) => Future[Result])
           (request: RequestHeader): Future[Result] = {
    if (!request.path.contains("/login") &&
      !request.path.endsWith(".css") &&
      !request.path.endsWith(".js") &&
      !request.path.endsWith(".png") &&
      !request.path.endsWith(".jpg")
    ) {
      Logger.logger.info("requestedPath=> " + request.path)
      request.cookies.get("token") match {
        case Some(ck) =>

          val decryptToken = EncryptUtil.decrypt(ck.value)
          if (decryptToken.contains("shopId") && decryptToken.contains("userId")) {
            next(request)
          } else {
            Future.successful(Results.Redirect("/login"))
          }

        case None => Future.successful(Results.Redirect("/login"))
      }
    } else {
      next(request)
    }

  }

}
