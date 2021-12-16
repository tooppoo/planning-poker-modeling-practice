package philomagi.dddcj.modeling.planning_poker
package table

import card.Card
import member.Member
import table.Table.CardOnTable

class Table private (
                      val id: Table.Id,
                      val members: List[Member],
                      val cards: List[CardOnTable]
                    ) {
  def accept(newMember: Member): Either[Exception, Table] = if (!members.contains(newMember)) {
    Right(new Table(id, members.appended(newMember), cards))
  } else {
    Left(new Exception(s"${newMember.id} already joined to table $id"))
  }

  def put(newCard: CardOnTable): Either[Exception, Table] = if (members.contains(newCard.owner)) {
    if (cards.contains(newCard)) {
      Right(replace(newCard))
    } else {
      Right(new Table(id, members, cards.appended(newCard)))
    }
  } else {
    Left(new Exception(s"${newCard.owner} not joined to table $id yet"))
  }

  def replace(newCard: CardOnTable): Table = withCards(
    cards.map(c => if (c.owner == newCard.owner) {
      newCard
    } else {
      c
    })
  )

  def withCards(cards: List[CardOnTable]) = new Table(id, members, cards)

  def toEmpty = new Table(id, List.empty, List.empty)
}
object Table {
  def apply(id: Table.Id, opener: Member) = new Table(id, List(opener), List.empty)

  case class Id(private val value: String)

  case class CardOnTable private (
                          owner: Member,
                          private val card: Card,
                          private val state: CardOnTable.State
                        ) {
    def suite: String = state match {
      case CardOnTable.State.Open => card.suite
      case CardOnTable.State.Close => "*"
    }

    def open: CardOnTable = CardOnTable(owner, card, CardOnTable.State.Open)
  }

  object CardOnTable {
    def apply(owner: Member, card: Card): CardOnTable = apply(owner, card, CardOnTable.State.Close)
    private[Table] def apply(owner: Member, card: Card, state: CardOnTable.State) =
      new CardOnTable(owner, card, state)

    sealed trait State
    object State {
      case object Open extends State
      case object Close extends State
    }
  }
}
