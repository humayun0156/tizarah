package models

import java.sql.Timestamp

/**
  * @author Humayun
  */
case class StockItemHistory(shopId: Long, date: Timestamp, history: String,
                            id: Option[Long] = None)


case class StockHistoryView(stockHistory: String)

case class StockItemHistoryView(itemName: String, amount: Double, transactionType: String,
                                date: Timestamp, description: String)