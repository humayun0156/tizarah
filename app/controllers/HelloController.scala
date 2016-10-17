package controllers

import play.api.mvc.{Action, Controller}

/**
  * @author Humayun
  */
class HelloController extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def blankpage = Action {
    Ok(views.html.blankpage())
  }

  def createAccountHead = Action {
    Ok(views.html.accounthead())
  }

  def debit = Action {
    Ok(views.html.debitTranscation())
  }

}
