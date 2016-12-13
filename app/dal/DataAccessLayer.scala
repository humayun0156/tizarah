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
    *************** User table ****************
    **********************************************/
  private class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id: Rep[Long] = column[Long]("user_id", O.PrimaryKey, O.AutoInc)
    def userName: Rep[String] = column[String]("user_name")
    def password: Rep[String] = column[String]("password")
    def displayName: Rep[String] = column[String]("display_name")

    def * = (userName, password, displayName, id.?) <> (User.tupled, User.unapply)
  }

  private val userTableQuery = TableQuery[UserTable]

  def getByUserNamePassword(userName: String, password: String): Option[User] = Await.result (
    db.run {
      userTableQuery.filter(x => x.userName === userName && x.password === password).result.headOption
    }, 50.milliseconds
  ) match {
    case Some(x) => Some(x)
    case None => None
  }

  def getAll(): Future[List[User]] = db.run {
    userTableQuery.to[List].result.statements foreach println
    userTableQuery.to[List].result
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
    *************** working_shop table ****************
    **********************************************/
  private class WorkingShopTable(tag: Tag) extends Table[WorkingShop](tag, "working_shop") {
    def shopId: Rep[Long] = column[Long]("shop_id")
    def userId: Rep[Long] = column[Long]("user_id")

    def shopIdFk = foreignKey("shop_id_fk", shopId, shopTableQuery)(_.id)
    def userIdFk = foreignKey("user_id_fk", userId, userTableQuery)(_.id)

    def * = (shopId, userId) <> (WorkingShop.tupled, WorkingShop.unapply)
  }
  private val workingShopTableQuery = TableQuery[WorkingShopTable]

  implicit val shopUserResult = GetResult(su => ShopUser(su.<<, su.<<, su.<<, su.<<, su.<<, su.<<))
  def getShopByUserId(userId: Long): Option[ShopUser] = {
    val query = sql"select s.shop_name, s.address, s.shop_id, w.user_id, u.user_name, u.display_name from shop s, working_shop w, user u where w.user_id=$userId and u.user_id=$userId;".as[ShopUser]
    Await.result(db.run(query), 2.seconds ).headOption
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

  def getTransactionsByAccountId(accountId: Long): List[Transaction] = {
    val query = db.run {
      transactionTableQuery.filter(t => t.accountId === accountId).to[List].result
    }
    Await.result(query, 100.milliseconds )
  }

  implicit val transactionViewResult = GetResult(t => TransactionView(t.<<, t.<<, t.<<, t.<<, t.<<, t.<<, t.<<, t.<<, t.<< ))
  def getTransactionById(transactionId: Long): Option[TransactionView] = {
    val query = sql"select t.transaction_id, t.description, t.date, t.transaction_type, t.amount, a.account_name, a.address, a.phone_number, h.head_name from transaction t, account a, account_head h where t.transaction_id = $transactionId and a.account_id = t.account_id and a.head_id = h.head_id".as[TransactionView]
    //transactionTableQuery.filter(t => t.id === transactionId).result.headOption
    Await.result(db.run(query), 2.seconds).headOption
  }

  implicit val journalRepResult = GetResult(t => JournalRep(t.<<, t.<<, t.<<, t.<<, t.<<))
  def getTodayTranByShopId(shopId: Long, journalDate: String): List[JournalRep] = {
    val query = sql"SELECT t.transaction_id, a.account_name, t.description, t.transaction_type, t.amount  from transaction t, account a where t.shop_id=$shopId and date(t.date)=$journalDate and t.account_id=a.account_id".as[JournalRep]
    Await.result(db.run(query), 2.seconds ).to[List]
  }

  implicit val ledgerIndexDataResult = GetResult(l => LedgerIndexData(l.<<, l.<<, l.<<, l.<<))
  def getLedgerIndexData(shopId: Long): List[LedgerIndexData] = {
    val query = sql"SELECT h.head_id, h.head_name, a.account_id, a.account_name from account a, account_head h where a.shop_id=$shopId and h.shop_id=$shopId and h.head_id=a.head_id".as[LedgerIndexData]
    Await.result(db.run(query), 3.seconds).to[List]
  }


  /**********************************************
    ********** Stock_Item table *****************
    **********************************************/
  private class StockItemTable(tag: Tag) extends Table[StockItem](tag, "stock_item") {
    def id: Rep[Long] = column[Long]("item_id", O.PrimaryKey, O.AutoInc)
    def itemName: Rep[String] = column[String]("item_name")
    def shopId: Rep[Long] = column[Long]("shop_id")
    def initialAmount: Rep[Double] = column[Double]("initial_amount")
    def importAmount: Rep[Double] = column[Double]("import_amount")
    def exportAmount: Rep[Double] = column[Double]("export_amount")

    def shopIdFk = foreignKey("shop_id_fk", shopId, shopTableQuery)(_.id)

    def * = (shopId, itemName, initialAmount, importAmount, exportAmount, id.?) <> (StockItem.tupled, StockItem.unapply)
  }

  private val stockItemTableQuery = TableQuery[StockItemTable]

  def insertStockItem(stockItem: StockItem): Future[Long] = db.run {
    stockItemTableQuery returning stockItemTableQuery.map(_.id) += stockItem
  }

  def getStockItems(shopId: Option[Long]): Future[List[StockItem]] = db.run {
    shopId match {
      case Some(x) => stockItemTableQuery.filter(stockItem => stockItem.shopId === x).to[List].result
      case None => stockItemTableQuery.to[List].result
    }

  }

  def updateStockItems(): Int = {
    val query = sql"update stock_item set initial_amount = initial_amount + import_amount - export_amount, import_amount = 0, export_amount = 0".asUpdate
    Await.result(db.run(query), 2.seconds)
  }


  /**********************************************
    ********** Stock_Transaction table **********
    **********************************************/
  private class StockTransactionTable(tag: Tag) extends Table[StockTransaction](tag, "stock_transaction") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def stockItemId: Rep[Long] = column[Long]("stock_item_id")
    def amount: Rep[Double] = column[Double]("amount")
    def importExport: Rep[String] = column[String]("import_export")
    def date: Rep[Timestamp] = column[Timestamp]("date")
    def description: Rep[String] = column[String]("description")

    def stockItemIdFk = foreignKey("stock_item_id_fk", stockItemId, stockItemTableQuery)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.NoAction)

    def * = (stockItemId, amount, importExport, date, description, id.?) <> (StockTransaction.tupled, StockTransaction.unapply)
  }

  private val stockTransactionTableQuery = TableQuery[StockTransactionTable]

  def insertStockTransaction(transaction: StockTransaction): Future[Long] = db.run {
    stockTransactionTableQuery returning stockTransactionTableQuery.map(_.id) += transaction
  }


  /**********************************************
    ********** Stock_Transaction table **********
    **********************************************/
  private class StockItemHistoryTable(tag: Tag) extends Table[StockItemHistory](tag, "stock_item_history") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def shopId: Rep[Long] = column[Long]("shop_id")
    def date: Rep[Timestamp] = column[Timestamp]("date")
    def history: Rep[String] = column[String]("history")

    def shopIdFk = foreignKey("shop_id_Fk", shopId, shopTableQuery)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.NoAction)

    def * = (shopId, date, history, id.?) <> (StockItemHistory.tupled, StockItemHistory.unapply)
  }

  private val stockItemHistoryTableQuery = TableQuery[StockItemHistoryTable]

  def insertStockHistory(stockHistory: StockItemHistory): Future[Long] = db.run {
    stockItemHistoryTableQuery returning  stockItemHistoryTableQuery.map(_.id) += stockHistory
  }

  implicit val stockHistoryViewResult = GetResult(t => StockHistoryView(t.<<))
  def getStockHistory(date: String, shopId: Long): Option[StockHistoryView]  = {
    //stockItemHistoryTableQuery.filter(stockItem => stockItem.date === date).to[List].result.headOption
    val query = sql"SELECT h.history FROM stock_item_history h WHERE date(h.date)=$date and h.shop_id=$shopId".as[StockHistoryView]
    Await.result(db.run(query), 2.seconds).to[List].headOption
  }

  implicit val stockItemHistoryViewResult = GetResult(t => StockItemHistoryView(t.<<, t.<<, t.<<, t.<<, t.<<))
  def getStockItemHistory(shopId: Long, itemId: Long): List[StockItemHistoryView] = {
    val query = sql"select s.item_name, t.amount, t.import_export, t.date, t.description from stock_item s, stock_transaction t where s.shop_id=$shopId and s.item_id=t.stock_item_id and s.item_id=$itemId order by t.date DESC".as[StockItemHistoryView]
    Await.result(db.run(query), 2.seconds).to[List]
  }

}
