package philomagi.dddcj.modeling.planning_poker
package domain.model.card

case class Card(suite: String) {
  require(suite.nonEmpty, "suite of card must not be empty")
}
