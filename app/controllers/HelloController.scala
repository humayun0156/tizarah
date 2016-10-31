package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import play.filters.csrf._
import play.filters.csrf.CSRF.Token

/**
  * @author Humayun
  */
class HelloController @Inject() (addToken: CSRFAddToken) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def blankpage = Action {
    Ok(views.html.blankpage())
  }

  def createAccountHead = addToken {
    Action { implicit request =>
      val Token(name, value) = CSRF.getToken.get
      Ok(views.html.accounthead(s"$name", s"$value"))
    }
  }

  def debit = Action {
    Ok(views.html.debitTranscation())
  }

  def journal = Action {
    Ok(views.html.journal())
  }
}
