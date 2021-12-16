package philomagi.dddcj.modeling.planning_poker
package table

import card.Card
import member.Member

class Table private (
                      private val id: Table.Id,
                      private val members: List[Member],
                      val cards: List[Card]
                    )
object Table {
  def apply(id: Table.Id, opener: Member) = new Table(id, List(opener), List.empty)

  case class Id(private val value: String)

  case class CardOnTable(
                          private val card: Card,
                          private val state: CardOnTable.State
                        )
  object CardOnTable {
    sealed trait State
    case object Open extends State
    case object Close extends State
  }
}
