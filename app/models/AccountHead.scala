package models

case class AccountHead(name: String, shopId: Long, id: Option[Long] = None)

//case class LedgerHeadAccount(name: String, id: Long)
case class LedgerAccount(id: Long, name: String)
case class LedgerIndexAccount(headId: Long, headName: String, accounts: List[LedgerAccount])
case class LedgerIndexData(headId: Long, headName: String, accountId: Long, accountName: String)

