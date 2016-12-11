package models


case class Shop(name: String, address: String, businessId: Long, id: Option[Long] = None)

case class WorkingShop(shopId: Long, userId: Long)

case class ShopUser(shopName: String, address: String, shopId: Long, userId: Long,
                    userName: String, displayName: String)
