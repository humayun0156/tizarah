package controllers

import javax.inject.Inject

import dal.{DataAccessLayer, UserRepository}
import play.api.Logger

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import utils.Utility._

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Humayun
  */
class MyTestController @Inject() (accessData: DataAccessLayer,
                                  userRepo: UserRepository)
                                 (implicit val ec: ExecutionContext) extends Controller{

  val logger = Logger(this.getClass)

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

}