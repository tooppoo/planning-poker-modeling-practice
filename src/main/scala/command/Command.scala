package philomagi.dddcj.modeling.planning_poker
package command

import table.Table

trait Command {

}
object Command {
  def dispatch(command: Command, table: Table): Table = table
}
