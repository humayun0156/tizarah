package controllers

import javax.inject.Inject

import dal.DataAccessLayer
import models.{LedgerAccount, LedgerIndexAccount, LedgerIndexData, Transaction}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import utils.LedgerAccountTransaction
import utils.Utility._

import scala.concurrent.{ExecutionContext, Future}

class LedgerController @Inject() (accessData: DataAccessLayer)
                                 (implicit val ec: ExecutionContext) extends Controller {

  def ledger = Action {
    Ok(views.html.ledgerindex())
  }

  def ledgerAccountPage(id: Long) = Action {
    Ok(views.html.ledgeraccount(id))
  }

  def ledgerIndexPage() = Action.async { request =>
    val data: List[LedgerIndexData] = accessData.getLedgerIndexData(getShopId(request))
    var headMap: Map[Long, List[LedgerAccount]]= Map()
    var headNameMap: Map[Long, String] = Map()
    for (   i <- data ) {
      if (headMap.contains(i.headId)) {
        val tmp: List[LedgerAccount] = LedgerAccount(i.accountId, i.accountName)::headMap(i.headId)
        headMap += (i.headId -> tmp)
      } else {
        val list = List(LedgerAccount(i.accountId, i.accountName))
        headMap += (i.headId -> list)
      }
      headNameMap += (i.headId -> i.headName)
    }
    println("HeadNameMap: " + headNameMap)

    val indexData = for (
      key <- headMap.keys
    ) yield LedgerIndexAccount(key, headNameMap(key), headMap(key))

    println(indexData)
    Future.successful(Ok(successResponse(Json.toJson(indexData), "")))
  }

  def ledgerAccountData(id: Long) = Action.async { request =>
    println("accountId: " + id)
    val data: List[Transaction] = accessData.getTransactionsByAccountId(id)
    val debitTotal: Double =
      data
        .filter(t => t.transactionType.equals("debit"))
        .map(t => t.amount).sum
    val creditTotal: Double =
      data
        .filter(t => t.transactionType.equals("credit"))
        .map(t => t.amount).sum

    val ledgerTransaction = LedgerAccountTransaction(data, debitTotal, creditTotal)
    Future.successful(Ok(successResponse(Json.toJson(ledgerTransaction), "")))
  }
}

