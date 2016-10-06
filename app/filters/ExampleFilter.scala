package filters

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */
class ExampleFilter@Inject()(implicit override val mat: Materializer,
                             exec: ExecutionContext) extends Filter {

  override def apply(f: (RequestHeader) => Future[Result])
                    (rh: RequestHeader): Future[Result] = {

    Logger.info(rh.path)

    f(rh).map(result => result.withHeaders("X-ExampleFilter" -> "foo"))
  }


}
