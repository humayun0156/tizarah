package dal

import javax.inject.{Inject, Singleton}

import models.Employee
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  * @author Humayun
  */
@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
                                  (implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    def email: Rep[String] = column[String]("email", O.SqlType("VARCHAR(200)"))
    def companyName: Rep[String] = column[String]("company_name")
    def position: Rep[String] = column[String]("position")
    def emailUnique = index("email_unique_key", email, unique = true)

    def * = (name, email, companyName, position, id.?) <> (Employee.tupled, Employee.unapply)
  }

  private val empTableQuery = TableQuery[EmployeeTable]

  def insert(employee: Employee): Future[Int] = db.run {
    (empTableQuery returning empTableQuery.map(_.id)) += employee
  }

  def insertAll(empList: List[Employee]):  Future[Seq[Int]] = db.run {
    (empTableQuery returning empTableQuery.map(_.id)) ++= empList
  }

  def update(employee: Employee): Future[Int] = db.run {
    empTableQuery.filter(_.id === employee.id).update(employee)
  }

  def delete(empId: Int): Future[Int] = db.run {
    empTableQuery.filter(_.id === empId).delete
  }

  def getAll(): Future[List[Employee]] = db.run {
    empTableQuery.to[List].result
  }

  def getById(empId: Int): Future[Option[Employee]] = db.run {
    empTableQuery.filter(_.id === empId ).result.headOption
  }

  def getByIdName(empId: Int, name: String): Future[Option[Employee]] = db.run {
    empTableQuery.filter(x => x.id === empId && x.name === name).result.headOption
  }

  //def ddl = empTableQuery.schema

}
