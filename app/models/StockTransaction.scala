package models

import java.sql.Timestamp

/**
  * @author Humayun
  */
case class StockTransaction(stockItemId: Long, amount: Double, importExport: String,
                            date: Timestamp, description: String, id: Option[Long] = None)
