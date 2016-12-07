package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

import dal.DataAccessLayer
import models.{StockItem, StockTransaction, Transaction}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import utils.{StockItemForm, StockTransactionForm, TransactionForm}
import utils.Utility._

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */

class StockController @Inject() (accessData: DataAccessLayer)
                                (implicit val ec: ExecutionContext) extends Controller {
  val logger = Logger(this.getClass)

  def index = Action {
    Ok(views.html.stock())
  }

  def getStockItems = Action.async { request =>
    val shopId = getShopId(request)
    accessData.getStockItems(shopId).map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def createStockItem() = Action.async(parse.json) { request =>
    logger.info("StockItem request body: " + request.body)
    request.body.validate[StockItemForm].fold(
      error => {
        logger.info("Error: " + error)
        Future.successful(Ok(successResponse(JsError.toJson(error), "There is an error", None)      ))
      },
      {
        stockItemForm =>
          val shopId = getShopId(request)
          val stockItem = StockItem(shopId, stockItemForm.itemName, stockItemForm.initialAmount.toDouble)
          accessData.insertStockItem(stockItem).map {
            stockId => Ok(successResponse(Json.toJson(Map("id" -> stockId)), "Stock item created successfully."))
          }
      }
    )
  }

  def createStockTransaction() = Action.async(parse.json) { request =>
    logger.info("StockTransaction request body: " + request.body)
    request.body.validate[StockTransactionForm].fold(
      error => {
        logger.info("Error: " + error)
        Future.successful(Ok(successResponse(JsError.toJson(error), "There is an error", None)      ))
      },
      {
        stForm => {
          val pattern = "dd/MM/yyyy"
          val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat(pattern)
          val date: Date = simpleDateFormat.parse(stForm.date)

          val ts = new Timestamp(date.getTime)
          val stockTransaction = StockTransaction(stForm.stockItemId.toLong, stForm.amount, stForm.importExport,
            ts, stForm.description)
          accessData.insertStockTransaction(stockTransaction).map {
            tranId => Ok(successResponse(Json.toJson(Map("id" -> tranId)), "Head Created Successfully."))
          }
        }
      }
    )
  }
}
