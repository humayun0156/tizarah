package dal
import javax.inject.{Inject, Singleton}

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
                               (implicit executionContext: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id: Rep[Long] = column[Long]("user_id", O.PrimaryKey, O.AutoInc)
    def userName: Rep[String] = column[String]("user_name")
    def password: Rep[String] = column[String]("password")

    def * = (userName, password, id.?) <> (User.tupled, User.unapply)
  }

  private val userTableQuery = TableQuery[UserTable]

  def getByUserNamePassword(userName: String, password: String): Option[User] = Await.result (
    db.run {
      userTableQuery.filter(x => x.userName === userName && x.password === password).result.headOption
    }, 50 millisecond
  ) match {
    case Some(x) => Some(x)
    case None => None
  }

  def getAll(): Future[List[User]] = db.run {
    userTableQuery.to[List].result.statements foreach println
    userTableQuery.to[List].result
  }
}
