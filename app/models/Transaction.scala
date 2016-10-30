package models

import java.sql.Timestamp

case class Transaction(shopId: Long, accountId: Long, description: String,
                       amount: Double, date: Timestamp, transactionType: String,
                       id: Option[Long] = None)

