package philomagi.dddcj.modeling.planning_poker.core
package sceinario

import domain.attendance.model.Attendance
import domain.card.model.Card
import domain.command.model.Command
import domain.command.model.Command.Commands
import domain.role.model.Role
import domain.table.model.Table

import org.scalatest.funspec.AnyFunSpec

class ScenarioTest extends AnyFunSpec {
  describe("プランニングポーカー実施") {
    val dispatcher = Command.Dispatcher.NoPersistenceDispatcher

    it("テーブル準備 ~ メンバー招待 ~ ポーカープレイ ~ 解散") {
      val facilitator = Attendance(
        Attendance.Id("aa63878a-e45d-d8f5-5b6e-abecc5afec4f"),
        Attendance.Name("John Doe"),
        List(Role.Facilitator, Role.Player)
      )

      val player2 = Attendance(
        Attendance.Id("7aedf045-136c-cbfd-f847-712771db88d8"),
        Attendance.Name("Jane Doe"),
        List(Role.Audience)
      )
      val player3 = Attendance(
        Attendance.Id("ec39a54e-fc7b-7f43-1cf6-4530750da528"),
        Attendance.Name("Ulick Norman Owen"),
        List(Role.Audience, Role.Player)
      )
      val player4 = Attendance(
        Attendance.Id("058f9c8e-5b4f-41d9-944d-7c145d675064"),
        Attendance.Name("Una Nancy Owen"),
        List(Role.Audience, Role.Player)
      )

      val tableAfterPlayed = for {
        table <- dispatcher.dispatch(
          Commands.SetUpNewTable(facilitator),
          Table(Table.Id("745b898c-ed06-0cf3-69bb-c7b4051d7d99"), facilitator)
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
          Commands.PutDownCard(player4, Card(Card.Suite("3"))),
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
