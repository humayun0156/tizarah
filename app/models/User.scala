package models

/**
  * @author Humayun
  */
case class User(username: String, password: String, displayName: String, id: Option[Long] = None)
