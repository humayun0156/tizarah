package controllers

import javax.inject.{Inject, Singleton}

import dal.DataAccessLayer
import utils.Constants._
import models.Business
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessController @Inject()(val messagesApi: MessagesApi, accessData: DataAccessLayer)
                                  (implicit ec: ExecutionContext) extends Controller with I18nSupport  {

  val logger = Logger(this.getClass())
  private def successResponse(data: JsValue, message: String) = {
    obj("status" -> SUCCESS, "data" -> data, "msg" -> message)
  }

  def index = Action { implicit request =>
    Ok(views.html.Business(businessForm))
    //Ok(views.html.Business())
  }

  def getBusinesses = Action.async { implicit request =>
    accessData.list().map {
      business => Ok(Json.toJson(business))
    }
  }

  def formSubmit = Action.async(parse.json) { request =>
    logger.info("Employee Json ===> " + request.body)
    request.body.validate[Business].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      { emp =>
        accessData.insert(emp).map { createdEmpId =>
        Ok(successResponse(Json.toJson(Map("id" ->createdEmpId)), "Employee has been created successfully."))
      }
    })
  }

  val businessForm: Form[BusinessForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(BusinessForm.apply)(BusinessForm.unapply)
  }

  /*def addBusiness = Action.async(parse.json) { implicit request =>
    logger.info("Employee Json ===> " + request.body)
    //repo.insert(Business())
  }*/

}

case class BusinessForm(name: String)