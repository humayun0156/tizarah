package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class TransactionController @Inject() () extends Controller {

  def debit = Action {
    Ok(views.html.transcation())
  }
}
