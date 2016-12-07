package models

/**
  * @author Humayun
  */
case class StockItem(shopId: Long, itemName: String, initialAmount: Double = 0.0,
                     importAmount: Double = 0.0, exportAmount: Double = 0.0,
                     id: Option[Long] = None)
