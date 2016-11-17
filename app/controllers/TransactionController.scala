package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

import dal.DataAccessLayer
import models.Transaction
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import utils.TransactionForm
import utils.Utility._

import scala.concurrent.{ExecutionContext, Future}

class TransactionController @Inject() (accessData: DataAccessLayer)
                                      (implicit val ec: ExecutionContext) extends Controller {
  val logger = Logger(this.getClass)


  def debit = Action {
    Ok(views.html.transcation())
  }

  def createTransaction() = Action.async(parse.json) { request =>
    logger.info("Transaction request body: " + request.body)
    request.body.validate[TransactionForm].fold(
      error => {
        logger.info("Error: " + error)
        Future.successful(Ok(successResponse(JsError.toJson(error), "There is an error", None)      ))
      },
      {
        transactionForm => {
          val shopId = getShopId(request)
          val pattern = "dd/MM/yyyy"
          val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat(pattern)
          val date: Date = simpleDateFormat.parse(transactionForm.date)

          val ts = new Timestamp(date.getTime)
          val transaction = Transaction(shopId, transactionForm.accountId.toLong,
            transactionForm.description, transactionForm.amount, ts, transactionForm.transactionType)
          accessData.insertTransaction(transaction).map {
            tranId => Ok(successResponse(Json.toJson(Map("id" -> tranId)), "Head Created Successfully."))
          }
        }
      }
    )
  }

  def view(id: Long) = Action {
    Ok(views.html.transaction_view(id))
  }

  def viewTransaction(id: Long) = Action.async { request =>
    logger.info("Requested transactionId: " + id)

    accessData.getTransactionById(id).map { res =>
      Future.successful(Ok(successResponse(Json.toJson(res), "")))
    }.getOrElse(Future.successful(Ok(successResponse(Json.toJson(""), "not found", None))))

    // Future[Option[Transaction]]
    /*accessData.getTransactionById(id).map {
      case Some(x) => Ok(successResponse(Json.toJson(x), ""))
      case None => Ok(successResponse(Json.toJson(""), ""))
    }.recover {case ex => Ok("fail")}*/

  }
}
