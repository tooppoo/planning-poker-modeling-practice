package philomagi.dddcj.modeling.planning_poker
package domain.model.table

import domain.model.card.Card
import domain.model.member.Member
import domain.model.table.Table.CardOnTable

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

  def put(newCard: Card, by: Member): Either[Exception, Table] = requireAlreadyJoin(by) {
    if (cards.contains(newCard)) {
      replace(newCard, by = by)
    } else {
      Right(new Table(
        id,
        members,
        cards.appended(CardOnTable(by, newCard))
      ))
    }
  }

  def replace(newCard: Card, by: Member): Either[Exception, Table] = requireAlreadyJoin(by) {
    Right(withCards(
      cards.map(c => if (c.owner == by) {
        CardOnTable(by, newCard)
      } else {
        c
      })
    ))
  }

  def openCards: Table = withCards(cards.map(c => c.open))

  def toEmpty: Table = new Table(id, List.empty, List.empty)

  private def withCards(cards: List[CardOnTable]) = new Table(id, members, cards)

  private def requireAlreadyJoin(m: Member)(r: Either[Exception, Table]): Either[Exception, Table] =
    if (members.contains(m)) {
      r
    } else {
      Left(new Exception(s"$m not joined to table $id yet"))
    }
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
      case CardOnTable.State.Open => card.suite.label
      case CardOnTable.State.Close => "*"
    }

    def open: CardOnTable = CardOnTable(owner, card, CardOnTable.State.Open)
  }

  object CardOnTable {
    def apply(owner: Member, card: Card): CardOnTable = apply(owner, card, CardOnTable.State.Close)
    private[CardOnTable] def apply(owner: Member, card: Card, state: CardOnTable.State) =
      new CardOnTable(owner, card, state)

    sealed trait State
    object State {
      case object Open extends State
      case object Close extends State
    }
  }
}
