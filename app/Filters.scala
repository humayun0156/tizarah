import com.google.inject.Inject
import filters.ExampleFilter
import play.api.{Environment, Mode}
import play.api.http.HttpFilters

/**
  * @author Humayun
  */
class Filters @Inject() (
      env: Environment,
      exampleFilter: ExampleFilter) extends HttpFilters {
  override val filters = {
    if (env.mode ==  Mode.Dev)
      Seq(exampleFilter)
    else
      Seq.empty
  }
}
