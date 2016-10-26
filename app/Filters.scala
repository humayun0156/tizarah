import com.google.inject.Inject
import filters.{AuthFilter, ExampleFilter, LoggingFilter}
import play.api.{Environment, Mode}
import play.api.http.HttpFilters

/**
  * @author Humayun
  */
class Filters @Inject() (env: Environment,
                         exampleFilter: ExampleFilter,
                         loggingFilter: LoggingFilter,
                         authFilter: AuthFilter) extends HttpFilters {
  override val filters = {
    if (env.mode ==  Mode.Dev)
      Seq(authFilter, exampleFilter/*, loggingFilter*/)
    else
      Seq.empty
  }
}
