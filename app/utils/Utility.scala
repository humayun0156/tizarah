package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import models.{Business, LedgerAccount, LedgerIndexAccount, Shop}
import play.api.libs.json.{Format, Writes, _}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.mvc.Request
import models._
/**
  * @author Humayun
  */
object Utility {

  implicit val businessFormat = Json.format[Business]
  implicit val shopFormat = Json.format[Shop]

  implicit val headFormat = Json.format[AccountHead]
  implicit val subAccountFormat = Json.format[SubAccount]

  implicit val headFormFormat = Json.format[HeadForm]
  implicit val subFormFormat = Json.format[SubAccountForm]

  implicit val rds: Reads[Timestamp] = (__ \ "time").read[Long].map{ long => new Timestamp(long) }
  implicit val wrs: Writes[Timestamp] = (__ \ "time").write[Long].contramap{ (a: Timestamp) => a.getTime }
  implicit val fmt: Format[Timestamp] = Format(rds, wrs)


  implicit val transactionForm = Json.format[TransactionForm]
  implicit val transactionFormat = Json.format[Transaction]
  implicit val transactionViewFormat = Json.format[TransactionView]

  implicit val journalRepFormat = Json.format[JournalRep]
  implicit val jTranFormat = Json.format[JournalTransaction]

  implicit val ledgerAccFormat = Json.format[LedgerAccount]
  implicit val ledgerIndexAccFormat = Json.format[LedgerIndexAccount]
  implicit val ledgerAccTransactionFormat = Json.format[LedgerAccountTransaction]



  def successResponse(data: JsValue, message: String, status: Option[String] = Some("success")) = {
    status match {
      case Some(x) => obj("status" -> x, "data" -> data, "msg" -> message)
      case None => obj("status" -> "error", "data" -> data, "msg" -> message)
    }
  }

  def getShopId[A](request: Request[A]): Long = {
    request.cookies.get("shopId") match {
      case Some(ck) => ck.value.toLong
    }
  }

  def formattedDateAsString(longDate: Long, pattern: String): String = {
    val sdf = new SimpleDateFormat(pattern)
    sdf.format(new Date(longDate))
  }
}

case class HeadForm(name: String)

case class SubAccountForm(headId: String, name: String, phoneNumber: String, address: String)

case class TransactionForm(accountId: String, date: String, transactionType: String,
                           description: String, amount: Double)

case class JournalTransaction(debit: List[JournalRep], credit: List[JournalRep],
                              debitTotal: Double, creditTotal: Double)


case class LedgerAccountTransaction(transactions: List[Transaction], debitTotal: Double,
                                    creditTotal: Double)