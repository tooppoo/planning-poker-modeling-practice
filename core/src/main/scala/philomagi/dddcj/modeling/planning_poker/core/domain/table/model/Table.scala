package philomagi.dddcj.modeling.planning_poker.core.domain.table.model

import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table.CardOnTable

class Table private (
                      val id: Table.Id,
                      val attendances: Seq[Attendance],
                      val cards: Seq[CardOnTable]
                    ) {
  protected[core] def accept(newMember: Attendance): Either[Exception, Table] =
    if (!attendances.contains(newMember)) {
      Right(new Table(id, attendances ++ Seq(newMember), cards))
    } else {
      Left(new Exception(s"${newMember.id} already joined to table $id"))
    }

  protected[core] def put(newCard: Card, by: Attendance): Either[Exception, Table] = requireAlreadyJoin(by) {
    val alreadyPutOnTable = cards.exists(_ putBy by)

    if (alreadyPutOnTable) {
      replace(newCard, by = by)
    } else {
      Right(new Table(
        id,
        attendances,
        cards ++ Seq(CardOnTable(by, newCard))
      ))
    }
  }

  protected[core] def openCards: Table = withCards(cards.map(c => c.open))

  protected[core] def toEmpty: Table = new Table(id, Seq.empty, Seq.empty)

  private def withCards(cards: Seq[CardOnTable]) = new Table(id, attendances, cards)

  private def replace(newCard: Card, by: Attendance): Either[Exception, Table] = requireAlreadyJoin(by) {
    Right(withCards(
      cards.map { c =>
        if (c putBy by) {
          CardOnTable(by, newCard)
        } else {
          c
        }
      }
    ))
  }

  private def requireAlreadyJoin(m: Attendance)(r: Either[Exception, Table]): Either[Exception, Table] =
    if (attendances.contains(m)) {
      r
    } else {
      Left(new Exception(s"$m not joined to table $id yet"))
    }
}
object Table {
  def apply(id: Table.Id, opener: Attendance) = new Table(id, Seq(opener), Seq.empty)

  case class Id(private val value: String)

  protected[table] case class CardOnTable private (
                                   owner: Attendance,
                                   private val card: Card,
                                   private val state: CardOnTable.State
                                 ) {
    def suite: String = state match {
      case CardOnTable.State.Open => card.suite.label
      case CardOnTable.State.Close => "*"
    }

    def open: CardOnTable = CardOnTable(owner, card, CardOnTable.State.Open)

    def putBy(who: Attendance): Boolean = owner == who
  }

  object CardOnTable {
    protected[table] def apply(owner: Attendance, card: Card): CardOnTable = apply(owner, card, CardOnTable.State.Close)

    private[CardOnTable] def apply(owner: Attendance, card: Card, state: CardOnTable.State) =
      new CardOnTable(owner, card, state)

    private[CardOnTable] sealed trait State
    private[CardOnTable] object State {
      case object Open extends State
      case object Close extends State
    }
  }
}