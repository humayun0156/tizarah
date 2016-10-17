package controllers

import javax.inject.{Inject, Singleton}

import dal.EmployeeRepository
import models.Employee
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.Constants

import scala.concurrent.Future

@Singleton
class EmployeeController @Inject()(empRepo: EmployeeRepository, webJarAssets: WebJarAssets) extends Controller {
  implicit val employeeFormat = Json.format[Employee]
  import Constants._
  val logger = Logger(this.getClass())

  def index = Action {
    Ok(views.html.employee.index(webJarAssets))
  }

  private def successResponse(data: JsValue, message: String) = {
    obj("status" -> SUCCESS, "data" -> data, "msg" -> message)
  }

  def list() = Action.async {
    empRepo.getAll().map { res =>
      logger.info("Emp list: " + res)
      Ok(successResponse(Json.toJson(res), "Getting Employee list successfully"))
    }
  }

  def create() = Action.async(parse.json) { request =>
    logger.info("Employee Json ===> " + request.body)
    empRepo.getByIdName(4, "kabir").map(x => x.map(y => println("====: " + y.companyName)).getOrElse())
    request.body.validate[Employee].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      {
        emp => empRepo.insert(emp).map {
          createdEmpId => Ok(successResponse(Json.toJson(Map("id" -> createdEmpId)), "Employee has been crated successfully"))
        }
      }
    )
  }

  def update = Action.async(parse.json) { request =>
    logger.info("Employee Json ===> " + request.body)
    request.body.validate[Employee].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      {
        emp => empRepo.update(emp).map {
          res => Ok(successResponse(Json.toJson("{}"), "Employee has been updated successfully."))
        }
      }
    )
  }

  def delete(empId: Int) = Action.async { request =>
    empRepo.delete(empId).map { _ =>
      Ok(successResponse(Json.toJson("{}"), "Employee has been deleted successfully."))
    }
  }

}
