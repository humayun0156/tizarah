package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import play.filters.csrf._
import play.filters.csrf.CSRF.Token

class AccountController @Inject() (addToken: CSRFAddToken) extends Controller {

  def createAccountHead = addToken {
    Action { implicit request =>
      val Token(name, value) = CSRF.getToken.get
      Ok(views.html.accounthead(s"$name", s"$value"))
    }
  }
}
