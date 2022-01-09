package philomagi.dddcj.modeling.planning_poker.core
package sceinario

import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command.Commands
import org.scalatest.funspec.AnyFunSpec
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table

class ScenarioTest extends AnyFunSpec {
  describe("プランニングポーカー実施") {
    val dispatcher = Command.Dispatcher.NoPersistenceDispatcher

    it("テーブル準備 ~ メンバー招待 ~ ポーカープレイ ~ 解散") {
      val facilitator = Attendance(
        Attendance.Id("1"),
        Attendance.Name("John Doe"),
        List(Role.Facilitator, Role.Player)
      )

      val player2 = Attendance(
        Attendance.Id("2"),
        Attendance.Name("Jane Doe"),
        List(Role.Audience)
      )
      val player3 = Attendance(
        Attendance.Id("3"),
        Attendance.Name("Ulick Norman Owen"),
        List(Role.Audience, Role.Player)
      )
      val player4 = Attendance(
        Attendance.Id("4"),
        Attendance.Name("Una Nancy Owen"),
        List(Role.Audience, Role.Player)
      )

      val tableAfterPlayed = for {
        table <- dispatcher.dispatch(
          Commands.SetUpNewTable(facilitator),
          Table(Table.Id("domain/table"), facilitator)
        )
        // プレイヤー参加
        table <- dispatcher.dispatch(
          Commands.Join(player2),
          table
        )
        table <- dispatcher.dispatch(
          Commands.Join(player3),
          table
        )
        table <- dispatcher.dispatch(
          Commands.Join(player4),
          table
        )
        // ポーカープレイ
        table <- dispatcher.dispatch(
          Commands.PutDownCard(facilitator, Card(Card.Suite("1"))),
          table
        )
        table <- dispatcher.dispatch(
          Commands.PutDownCard(player3, Card(Card.Suite("2"))),
          table
        )
        table <- dispatcher.dispatch(
          Commands.PutDownCard(player4, Card(Card.Suite("1"))),
          table
        )
        table <- dispatcher.dispatch(
          Commands.ChangeCardOnTable(player4, Card(Card.Suite("3"))),
          table
        )
      } yield {
        assert(table.cards.map(c => c.suite).mkString(" ") == "* * *")

        table
      }

      val tableAfterShowDown = for {
        table <- tableAfterPlayed
        table <- dispatcher.dispatch(
          Commands.ShowDown(facilitator),
          table
        )
      } yield {
        assert(table.cards.map(c => c.suite).mkString(" ") == "1 2 3")

        table
      }

      for {
        table <- tableAfterShowDown
        table <- dispatcher.dispatch(
          Commands.CloseTable(facilitator),
          table
        )
      } {
        assert(table.cards.isEmpty)
        assert(table.attendances.isEmpty)
      }
    }
  }
}
