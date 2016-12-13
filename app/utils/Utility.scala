package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import models.{Business, LedgerAccount, LedgerIndexAccount, Shop}
import play.api.libs.json.{Format, Writes, _}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.mvc.{Request, Results}
import models._

import scala.concurrent.Future
/**
  * @author Humayun
  */
object Utility {

  implicit val rds: Reads[Timestamp] = (__ \ "time").read[Long].map{ long => new Timestamp(long) }
  implicit val wrs: Writes[Timestamp] = (__ \ "time").write[Long].contramap{ (a: Timestamp) => a.getTime }
  implicit val fmt: Format[Timestamp] = Format(rds, wrs)

  implicit val businessFormat = Json.format[Business]
  implicit val shopFormat = Json.format[Shop]

  implicit val headFormat = Json.format[AccountHead]
  implicit val subAccountFormat = Json.format[SubAccount]

  implicit val headFormFormat = Json.format[HeadForm]
  implicit val subFormFormat = Json.format[SubAccountForm]

  implicit val stockItemFormFormat = Json.format[StockItemForm]
  implicit val stockItemFormat = Json.format[StockItem]
  implicit val stockTransactionFormFormat = Json.format[StockTransactionForm]
  implicit val stockHistoryViewFormat = Json.format[StockHistoryView]
  implicit val stockItemHistoryViewFormat = Json.format[StockItemHistoryView]


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
    request.cookies.get("token") match {
      case Some(ck) => parseCookie(ck.value, "shopId").toLong
      case None => 0L
    }
  }

  def getUserId[A](request: Request[A]): Long = {
    request.cookies.get("token") match {
      case Some(ck) => parseCookie(ck.value, "userId").toLong
      case None => 0L
    }
  }

  def parseCookie(tkValue: String, key: String): String = {
    // shopId=23;userId=34
    val decryptToken = EncryptUtil.decrypt(tkValue)

    var x: String = ""
    if (decryptToken.contains("shopId") && decryptToken.contains("userId")) {
      val arr1: Array[String] = decryptToken.split(";")
      for (suId <- arr1) {
        if (suId.startsWith(key)) {
          val arr2: Array[String] = suId.split("=")
          if (arr2.length > 1) {
            x = arr2(1)
          }
        }
      }
    }
    x
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

case class StockItemForm(itemName: String, initialAmount: Double)

case class StockTransactionForm(stockItemId: String, amount: Double, importExport: String,
                                date: String, description: String = "")