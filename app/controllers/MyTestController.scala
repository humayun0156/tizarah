package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

import dal.{DataAccessLayer, UserRepository}
import models._
import play.api.Logger
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller, Request, RequestHeader}
import play.filters.csrf._
import play.filters.csrf.CSRF.Token

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */
class MyTestController @Inject() (accessData: DataAccessLayer,
                                  userRepo: UserRepository,
                                  checkToken: CSRFCheck)
                                 (implicit val ec: ExecutionContext) extends Controller{

  val logger = Logger(this.getClass())
  implicit val businessFormat = Json.format[Business]
  implicit val shopFormat = Json.format[Shop]
  implicit val headFormat = Json.format[AccountHead]
  implicit val subAccountFormat = Json.format[SubAccount]
  implicit val headFormFormat = Json.format[HeadForm]
  implicit val subFormFormat = Json.format[SubAccountForm]
  implicit val transactionForm = Json.format[TransactionForm]


  private def successResponse(data: JsValue, message: String, status: Option[String] = Some("success")) = {
    status match {
      case Some(x) => obj("status" -> x, "data" -> data, "msg" -> message)
      case None => obj("status" -> "error", "data" -> data, "msg" -> message)
    }

  }

  def allBusiness() = Action.async { request =>
    accessData.list().map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def allShop() = Action.async { request =>
    accessData.getAllShop().map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def allAccountHead() = Action.async { request =>
    accessData.headList().map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def headsByShopId(shopId: Long) = Action.async { request =>
    accessData.headsByShopId(shopId).map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def subAccountByHeadId(headId: Long) = Action.async { request =>
    accessData.getSubAccountByHeadId(headId).map { res =>
      Ok(successResponse(Json.toJson(res), "subAccount Message"))
    }
  }

  def getShopId[A](request: Request[A]): Long = {
    request.cookies.get("shopId") match {
      case Some(ck) => ck.value.toLong
    }
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

  def createSubAccount() = Action.async(parse.json) { request =>
    logger.info("SubAccount POST body json => " + request.body)
    request.body.validate[SubAccountForm].fold(
      error => {
        logger.info("Error: " + error)
        Future.successful(BadRequest(JsError.toJson(error)))
      },
      {
        subAcc => {
          val shopId = getShopId(request)
          val subAccount = SubAccount(subAcc.name, subAcc.address, subAcc.phoneNumber,
            subAcc.headId.toLong, shopId)
          accessData.insertSubAccount(subAccount).map {
            createdSubAccId => Ok(successResponse(Json.toJson(Map("id" -> createdSubAccId)), "Head Created Successfully."))
          }
        }
      }
    )
  }

  def createAccountHead() =  /*checkToken*/ {
    Action.async(parse.json) { implicit request =>
      logger.info("Head POST body json ==> " + request.body)
      request.body.validate[HeadForm].fold(
        error => {
          logger.info("Error: " + error)
          Future.successful(BadRequest(JsError.toJson(error)))
        },
        {
          accHead => {
            val shopId = getShopId(request)
            val x = AccountHead(accHead.name, shopId)
            accessData.insertAccHead(x).map {
              createdHeadId => Ok(successResponse(Json.toJson(Map("id" -> createdHeadId)), "Head Created Successfully."))
            }
          }
        }
      )
    }
  }

}

case class HeadForm(name: String)
case class SubAccountForm(headId: String, name: String, phoneNumber: String, address: String)
case class TransactionForm(accountId: String, date: String, transactionType: String,
                           description: String, amount: Double)