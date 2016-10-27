package models

case class SubAccount(name: String, address: String,
                      phoneNumber: String, headId: Long,
                      shopId: Long, id: Option[Long] = None) {

}
