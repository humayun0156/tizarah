package dal

import javax.inject.Inject

import models.{AccountHead, Business, Shop, SubAccount}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

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

}
