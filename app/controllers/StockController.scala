package controllers

import javax.inject.Inject

import dal.DataAccessLayer
import models.StockItem
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import utils.StockItemForm
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
          val stockItem = StockItem(shopId, stockItemForm.itemName)
          accessData.insertStockItem(stockItem).map {
            stockId => Ok(successResponse(Json.toJson(Map("id" -> stockId)), "Stock item created successfully."))
          }
      }
    )
  }
}
