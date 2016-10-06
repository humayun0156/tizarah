package controllers

import play.api.mvc.{Action, Controller}

/**
  * @author Humayun
  */
class HelloController extends Controller {

  def blankpage = Action {
    Ok(views.html.blankpage())
  }

  def createAccountHead = Action {
    Ok(views.html.accounthead())
  }

}
