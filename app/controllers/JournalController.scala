package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class JournalController @Inject() () extends Controller {

  def journal = Action {
    Ok(views.html.journal())
  }
}
