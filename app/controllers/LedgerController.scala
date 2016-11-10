package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class LedgerController @Inject() () extends Controller {

  def ledger = Action {
    Ok(views.html.ledgerindex())
  }

  def ledgerAccount(id: Long) = Action {
    Ok(views.html.ledgeraccount(id))
  }
}
