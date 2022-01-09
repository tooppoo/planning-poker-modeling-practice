package philomagi.dddcj.modeling.planning_poker
package domain.model.card

case class Card(suite: Card.Suite)
object Card {
  case class Suite(label: String) {
    require(label.nonEmpty, "suite of card must not be empty")
    require(
      label.length < Suite.MaxLength,
      s"max length of suite-label is ${Suite.MaxLength}, but actual is $label"
    )
  }
  object Suite {
    val MaxLength = 10
  }
}
