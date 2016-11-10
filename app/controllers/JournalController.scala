package controllers

import javax.inject.Inject

import dal.DataAccessLayer
import models.JournalRep
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import utils.JournalTransaction
import utils.Utility._

class JournalController @Inject() (accessData: DataAccessLayer)
                                  (implicit val ec: ExecutionContext) extends Controller {

  val logger = Logger(this.getClass)

  def journal = Action {
    Ok(views.html.journal())
  }

  def todayJournal(date: Long) = Action.async { request =>

    logger.info("DateRequested for journal: " + date)
    val dd: String = formattedDateAsString(date, "yyyy-MM-dd")
    logger.info("FormattedDate of journal: " + dd)

    val data: List[JournalRep] = accessData.getTodayTranByShopId(getShopId(request), dd)

    val debitTran: List[JournalRep] = data.filter(t => t.transactionType.equals("debit"))
    val creditTran: List[JournalRep] = data.filter(t => t.transactionType.equals("credit"))

    val debitTotal: Double = debitTran.map(t => t.amount).sum
    val creditTotal: Double = creditTran.map(t => t.amount).sum
    logger.info("debitTotal: " + debitTotal + ", creditTotal: " + creditTotal)
    val jTran: JournalTransaction = JournalTransaction(debitTran, creditTran, debitTotal, creditTotal)
    logger.info("debitTransaction: "+ data )
    Future.successful(Ok(successResponse(Json.toJson(jTran), "")))
  }
}
