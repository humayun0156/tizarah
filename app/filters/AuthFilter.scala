package filters

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */
class AuthFilter @Inject()(implicit override val mat: Materializer,
                           exec: ExecutionContext) extends Filter {

  def apply(next: (RequestHeader) => Future[Result])
           (request: RequestHeader): Future[Result] = {
    if (!request.path.contains("/login")) {
      request.cookies.get("shopId") match {
        case Some(ck) =>
          next(request)
        case None => Future.successful(Results.Redirect("/login"))
      }
    } else {
      next(request)
    }

  }

}
