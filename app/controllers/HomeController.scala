package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class HomeController @Inject() () extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def blankpage = Action {
    Ok(views.html.blankpage())
  }
}
