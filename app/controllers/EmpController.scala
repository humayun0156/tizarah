package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class EmpController @Inject() () extends Controller {

  def index = Action {
    Ok(views.html.empindex())
  }

  def create = Action {
    Ok(views.html.empindex())
  }
}
