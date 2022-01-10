package philomagi.dddcj.modeling.planning_poker.core
package domain.card.model

case class Card(suite: Card.Suite)
object Card {
  case class Suite(label: String) {
    import lib.extension.StringExtension._

    require(label.nonEmpty, "suite of card must not be empty")
    require(label.nonBlank, "suite of card must not be blank")
    require(label.length <= Suite.MaxLength, s"max length of suite-label is ${Suite.MaxLength}")
    require(label.notContains(Card.Suite.invalidChars), "card suite include invalid character")
  }
  object Suite {
    val MaxLength = 5

    //noinspection DuplicatedCode
    private val invalidChars = Seq(
      '!', '"', '#', '$', '%', '&', '\'', '(', ')', '|',
      '¥', '`', '@', '{', '}', '[', ']', '+', '*',
      '<', '>', '?', '/', '^', '\t', '\\'
    )
  }
}