package models

/**
  * @author Humayun
  */
case class User(username: String, password: String, id: Option[Long] = None)
