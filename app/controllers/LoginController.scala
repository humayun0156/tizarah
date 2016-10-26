package controllers

import javax.inject.{Inject, Singleton}

import dal.UserRepository
import models.User
import play.api.Logger
import play.api.libs.json.{JsResult, JsSuccess, Json, Reads}
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller, Cookie, DiscardingCookie}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


@Singleton
class LoginController@Inject() (userRepo: UserRepository)(implicit val ec: ExecutionContext) extends Controller {
  implicit val userFormat = Json.format[User]

  val logger = Logger(this.getClass())

  def login = Action { request =>
    request.cookies.get("shopId") match {
      case Some(x) => Redirect(routes.HelloController.index())
      case None => Ok(views.html.login("Login")).withNewSession
    }
  }

  def logout = Action { request =>
    Redirect(routes.LoginController.login()).discardingCookies(DiscardingCookie("shopId"))
  }

  // Shows the login screen and empties the session:
  def submit = Action(parse.json) { request =>
    // Creates a reader for the JSON - turns it into a LoginRequest
    //implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]
    implicit val loginRequest = Json.format[LoginRequest]
    val jsResult: JsResult[LoginRequest] = request.body.validate[LoginRequest]

    jsResult match {
      case s: JsSuccess[LoginRequest]  => {
        s.get.valid.map { x =>
          Ok(toJson(Map("valid" -> true)))
            .withSession("user" -> s.get.username)
            .withCookies(Cookie("shopId", "1", Some(3600)))

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


