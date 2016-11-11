package controllers

import javax.inject.Inject

import dal.DataAccessLayer
import models.{AccountHead, SubAccount}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.filters.csrf._
import play.filters.csrf.CSRF.Token
import utils._
import utils.Utility._

import scala.concurrent.{ExecutionContext, Future}

class AccountController @Inject() (addToken: CSRFAddToken, accessData: DataAccessLayer)
                                  (implicit val ec: ExecutionContext) extends Controller {

  val logger = Logger(this.getClass)

  def accountHead = addToken {
    Action { implicit request =>
      val Token(name, value) = CSRF.getToken.get
      Ok(views.html.accounthead(s"$name", s"$value"))
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

  def headAccounts() = Action.async { request =>
    accessData.headsByShopId(getShopId(request)).map { res =>
      Ok(successResponse(Json.toJson(res), ""))
    }
  }

  def subAccountByHeadId(headId: Long) = Action.async { request =>
    accessData.getSubAccountByHeadId(headId).map { res =>
      Ok(successResponse(Json.toJson(res), "subAccount Message"))
    }
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

  def createHeadAccount() =  /*checkToken*/ {
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
