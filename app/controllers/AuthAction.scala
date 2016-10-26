package controllers


import play.api.mvc.{ActionBuilder, Request, Result}

import scala.concurrent.Future

/**
  * @author Humayun
  */
object AuthAction extends ActionBuilder[Request] {

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    if (request.session.isEmpty) {
      //authentication condition not met - redirect to login page
      Future.successful(Redirect("/login"))
    } else {
      //proceed with action as normal
      block(request)
    }
  }

}
