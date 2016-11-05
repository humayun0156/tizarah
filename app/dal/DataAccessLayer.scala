package dal

import java.sql.Timestamp
import javax.inject.Inject

import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationDouble

/**
  * @author Humayun
  */
class DataAccessLayer @Inject() (dbConfigProvider: DatabaseConfigProvider)
                                (implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  /**********************************************
  *************** Business table ****************
  **********************************************/
  private class BusinessTable(tag: Tag) extends Table[Business](tag, "business") {
    def businessId = column[Long]("business_id", O.PrimaryKey, O.AutoInc)
    def businessName = column[String]("business_name")

    def * = (businessName, businessId) <> ((Business.apply _).tupled, Business.unapply )
  }

  //The starting point for all queries on the business table.
  private val businessTableQuery = TableQuery[BusinessTable]

  def insert(business: Business): Future[Long] = db.run {
    (businessTableQuery returning businessTableQuery.map(_.businessId)) += business
  }

  def list(): Future[List[Business]] = db.run {
    businessTableQuery.to[List].result
  }


  /**********************************************
    *************** Shop table ****************
    **********************************************/
  private class ShopTable(tag: Tag) extends Table[Shop](tag, "shop") {
    def id: Rep[Long] = column[Long]("shop_id", O.PrimaryKey, O.AutoInc)
    def shopName: Rep[String] = column[String]("shop_name")
    def shopAddress: Rep[String] = column[String]("address")
    def businessId: Rep[Long] = column[Long]("business_id")
    def businessIdFk =
      foreignKey("business_id_fk", businessId, businessTableQuery)(_.businessId)

    def * = (shopName, shopAddress, businessId, id.?) <> ((Shop.apply _).tupled, Shop.unapply)
  }

  // Starting point for all queries on the shop table
  private val shopTableQuery = TableQuery[ShopTable]

  def getAllShop(): Future[List[Shop]]= db.run {
    shopTableQuery.to[List].result
  }


  /**********************************************
    ********** AccountHead table ****************
    **********************************************/
  private class AccountHeadTable(tag: Tag) extends Table[AccountHead](tag, "account_head") {
    def id: Rep[Long]     = column[Long]("head_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("head_name")
    def shopId: Rep[Long] = column[Long]("shop_id")
    // for now don't adding the foreignKey constraint
    def shopIdFk = foreignKey("shop_id_fk", shopId, shopTableQuery)(_.id)

    def * = (name, shopId, id.?) <> (AccountHead.tupled, AccountHead.unapply)
  }

  // Starting point of all queries on Shop table
  private val accountHeadTableQuery = TableQuery[AccountHeadTable]

  def headList(): Future[List[AccountHead]] = db.run {
    accountHeadTableQuery.to[List].result
  }

  def headsByShopId(shopId: Long): Future[List[AccountHead]] = db.run {
    accountHeadTableQuery.filter(accountHead => accountHead.shopId === shopId).to[List].result
  }

  def insertAccHead(head: AccountHead): Future[Long] = db.run {
    (accountHeadTableQuery returning accountHeadTableQuery.map(_.id)) += head
  }


  /**********************************************
    ********** SubAccount table *****************
    **********************************************/
  private class SubAccountTable(tag: Tag) extends Table[SubAccount](tag, "account") {
    def id: Rep[Long] = column[Long]("account_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("account_name")
    def address: Rep[String] = column[String]("address")
    def phoneNumber: Rep[String] = column[String]("phone_number")
    def headId: Rep[Long] = column[Long]("head_id")
    def shopId: Rep[Long] = column[Long]("shop_id")

    def headIdFk = foreignKey("head_id_fk", headId, accountHeadTableQuery)(_.id)
    def shopIdFk = foreignKey("shop_id_fk", shopId, shopTableQuery)(_.id)

    def * = (name, address, phoneNumber, headId, shopId, id.?) <> (SubAccount.tupled, SubAccount.unapply)
  }

  private val subAccountTableQuery = TableQuery[SubAccountTable]

  def insertSubAccount(subAccount: SubAccount): Future[Long] = db.run {
    subAccountTableQuery returning subAccountTableQuery.map(_.id) += subAccount
  }

  def getSubAccountByHeadId(headId: Long): Future[List[SubAccount]] = db.run {
    subAccountTableQuery.filter(subAcc => subAcc.headId === headId).to[List].result
  }


  /**********************************************
    ********** Transaction table *****************
    **********************************************/
  private class TransactionTable(tag: Tag) extends Table[Transaction](tag, "transaction") {
    def id: Rep[Long] = column[Long]("transaction_id", O.PrimaryKey, O.AutoInc)
    def accountId: Rep[Long] = column[Long]("account_id")
    def shopId: Rep[Long] = column[Long]("shop_id")
    def description: Rep[String] = column[String]("description")
    def amount: Rep[Double] = column[Double]("amount")
    def transactionType: Rep[String] = column[String]("transaction_type")
    //FIXME works only MySQL?
    def date: Rep[Timestamp] = column[Timestamp]("date")

    def accountIdFk = foreignKey("account_id_fk", accountId, subAccountTableQuery)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.NoAction)
    def shopIdFk = foreignKey("shop_id_fk", shopId, shopTableQuery)(_.id)

    def * = (shopId, accountId, description, amount, date, transactionType, id.?) <> (Transaction.tupled, Transaction.unapply)
  }

  private val transactionTableQuery = TableQuery[TransactionTable]

  def insertTransaction(transaction: Transaction): Future[Long] = db.run {
    transactionTableQuery returning transactionTableQuery.map(_.id) += transaction
  }

  //implicit val transactionResult = GetResult(t => Transaction(t.<<, t.<<, t.<<, t.<<, t.<<, t.<<, t.<<))
  /*implicit val tR = GetResult(t =>
    Transaction(t.nextLong(), t.nextLong, t.nextString, t.nextDouble(),
      t.nextTimestamp(), t.nextString, t.nextLongOption()))*/
  implicit val journalRepResult = GetResult(t => JournalRep(t.<<, t.<<, t.<<, t.<<, t.<<))
  def getTodayTranByShopId(shopId: Long, journalDate: String): List[JournalRep] = {
    //val query = sql"select t.description, t.amount,  from TRANSACTION t WHERE t.shop_id=$shopId and date(date)=$journalDate".as[Transaction]
    val query = sql"SELECT t.transaction_id, a.account_name, t.description, t.transaction_type, t.amount  from transaction t, account a where t.shop_id=$shopId and date(t.date)=$journalDate and t.account_id=a.account_id".as[JournalRep]
    Await.result(db.run(query), 2 seconds ).to[List]
  }

}
