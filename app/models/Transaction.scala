package models

import java.sql.Timestamp

case class Transaction(shopId: Long, accountId: Long, description: String,
                       amount: Double, date: Timestamp, transactionType: String,
                       id: Option[Long] = None)

case class JournalRep(transactionId: Long, accountName: String, tranDescription: String,
                      transactionType: String, amount: Double)

