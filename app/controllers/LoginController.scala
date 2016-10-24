package controllers

import javax.inject.{Inject, Singleton}

import dal.UserRepository
import models.User
import play.api.Logger
import play.api.libs.json.{JsResult, JsSuccess, Json, Reads}
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


@Singleton
class LoginController@Inject() (userRepo: UserRepository)(implicit val ec: ExecutionContext) extends Controller {
  implicit val userFormat = Json.format[User]

  val logger = Logger(this.getClass())

  def login = Action {
    Ok(views.html.login("Login")).withNewSession
  }

  // Shows the login screen and empties the session:
  def submit = Action(parse.json) { request =>
    // Creates a reader for the JSON - turns it into a LoginRequest
    //implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]
    implicit val loginRequest = Json.format[LoginRequest]
    val jsResult: JsResult[LoginRequest] = request.body.validate[LoginRequest]

    jsResult match {
      case s: JsSuccess[LoginRequest] if (s.get.authenticate) => {
        s.get.valid
        Ok(toJson(Map("valid" -> true))).withSession("user" -> s.get.username)
      }
      case _ => Ok(toJson(Map("valid" -> false)))
    }
  }

  case class LoginRequest(username: String, password: String) {

    // Simple username-password map in place of a database:
    val validUsers = Map("sysadmin" -> "password1", "root" -> "god", "tizarah" -> "tizarah")

    def authenticate = validUsers.exists(_ == (username, password))
    def valid = {
      println("============Here=====")
      val x = userRepo.getByUserNamePassword(username, password).foreach(println)
      val y = userRepo.getAll().foreach(println)
      println("============End=====")
    }
  }
}


