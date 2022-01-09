package philomagi.dddcj.modeling.planning_poker.core.domain.table.model

import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table.CardOnTable

class Table private (
                      val id: Table.Id,
                      val members: List[Attendance],
                      val cards: List[CardOnTable]
                    ) {
  def accept(newMember: Attendance): Either[Exception, Table] = if (!members.contains(newMember)) {
    Right(new Table(id, members.appended(newMember), cards))
  } else {
    Left(new Exception(s"${newMember.id} already joined to table $id"))
  }

  def put(newCard: Card, by: Attendance): Either[Exception, Table] = requireAlreadyJoin(by) {
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

  def replace(newCard: Card, by: Attendance): Either[Exception, Table] = requireAlreadyJoin(by) {
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

  private def requireAlreadyJoin(m: Attendance)(r: Either[Exception, Table]): Either[Exception, Table] =
    if (members.contains(m)) {
      r
    } else {
      Left(new Exception(s"$m not joined to table $id yet"))
    }
}
object Table {
  def apply(id: Table.Id, opener: Attendance) = new Table(id, List(opener), List.empty)

  case class Id(private val value: String)

  case class CardOnTable private (
                                   owner: Attendance,
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
    def apply(owner: Attendance, card: Card): CardOnTable = apply(owner, card, CardOnTable.State.Close)
    private[CardOnTable] def apply(owner: Attendance, card: Card, state: CardOnTable.State) =
      new CardOnTable(owner, card, state)

    sealed trait State
    object State {
      case object Open extends State
      case object Close extends State
    }
  }
}