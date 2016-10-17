package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Business

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class BusinessRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

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
}
