package controllers

import javax.inject.{Inject, Singleton}

import dal.DataAccessLayer
import models.{ShopUser, User}
import play.api.Logger
import play.api.libs.json.{JsResult, JsSuccess, Json}
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller, Cookie, DiscardingCookie}
import utils.EncryptUtil

import scala.concurrent.ExecutionContext


@Singleton
class LoginController@Inject() (userRepo: DataAccessLayer)(implicit val ec: ExecutionContext) extends Controller {
  implicit val userFormat = Json.format[User]

  val logger = Logger(this.getClass)

  def login = Action { request =>
    request.cookies.get("shopId") match {
      case Some(x) => Redirect(routes.HomeController.index())
      case None => Ok(views.html.login("Login")).withNewSession
    }
  }

  def logout = Action { request =>
    Redirect(routes.LoginController.login()).discardingCookies(
      DiscardingCookie("token"),
      DiscardingCookie("shopName"),
      DiscardingCookie("userName")
    )
  }

  // Shows the login screen and empties the session:
  def submit = Action(parse.json) { request =>
    // Creates a reader for the JSON - turns it into a LoginRequest
    //implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]
    implicit val loginRequest = Json.format[LoginRequest]
    val jsResult: JsResult[LoginRequest] = request.body.validate[LoginRequest]

    jsResult match {
      case s: JsSuccess[LoginRequest]  => {
        s.get.valid.map { user =>
          val info: Option[ShopUser] = userRepo.getShopByUserId(user.id.get)

          info.map { shopUser =>
            logger.info("===>shopId=" + shopUser.shopId.toString + ";userId=" + shopUser.userId.toString)
            logger.info("shopName: " + shopUser.shopName + ";displayName: " + shopUser.displayName)
            val token = EncryptUtil.encrypt("shopId=" + shopUser.shopId.toString + ";userId=" + shopUser.userId.toString)
            logger.info("Token: " + token)
            Ok(toJson(Map("valid" -> true)))
              .withSession("user" -> s.get.username)
              .withCookies(
                Cookie("token", token, Some(3600)),
                Cookie("shopName", shopUser.shopName, Some(3600)),
                Cookie("userName", shopUser.displayName, Some(3600)))
          }.getOrElse(
            Ok(toJson(Map("valid" -> false)))
          )
        }.getOrElse(
          Ok(toJson(Map("valid" -> false)))
        )
      }
      case _ => Ok(toJson(Map("valid" -> false)))
    }
  }

  case class LoginRequest(username: String, password: String) {

    // Simple username-password map in place of a database:
    val validUsers = Map("sysadmin" -> "password1", "root" -> "god", "tizarah" -> "tizarah")

    def authenticate = validUsers.exists(_ == (username, password))
    def valid: Option[User] = {
      println("============Here=====")
      val x = userRepo.getByUserNamePassword(username, password)
      val y = userRepo.getAll().foreach(println)
      println("============End=====")
      x
    }
  }
}


