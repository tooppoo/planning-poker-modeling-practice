package philomagi.dddcj.modeling.planning_poker
package scenario

import card.Card
import command.Command
import member.Member
import table.Table

import org.scalatest.funspec.AnyFunSpec

class ScenarioTest extends AnyFunSpec {

  describe("プランニングポーカー実施") {
    val dispatcher = Command.Dispatcher.NoPersistenceDispatcher

    it("テーブル準備 ~ メンバー招待 ~ ポーカープレイ ~ 解散") {
      val facilitator = Member(
        Member.Id("1"),
        Member.Name("John Doe"),
        List(Member.Role.Facilitator, Member.Role.Player)
      )

      val player2 = Member(
        Member.Id("2"),
        Member.Name("Jane Doe"),
        List(Member.Role.Audience)
      )
      val player3 = Member(
        Member.Id("3"),
        Member.Name("Ulick Norman Owen"),
        List(Member.Role.Audience, Member.Role.Player)
      )
      val player4 = Member(
        Member.Id("4"),
        Member.Name("Una Nancy Owen"),
        List(Member.Role.Audience, Member.Role.Player)
      )

      val tableAfterPlayed = for {
        table <- dispatcher.dispatch(
          Member.Role.Facilitator.Commands.SetUpNewTable(facilitator),
          Table(Table.Id("table"), facilitator)
        )
        // プレイヤー参加
        table <- dispatcher.dispatch(
          Member.Role.NewComer.Commands.Join(player2),
          table
        )
        table <- dispatcher.dispatch(
          Member.Role.NewComer.Commands.Join(player3),
          table
        )
        table <- dispatcher.dispatch(
          Member.Role.NewComer.Commands.Join(player4),
          table
        )
        // ポーカープレイ
        table <- dispatcher.dispatch(
          Member.Role.Player.Commands.PutDownCard(facilitator, Card("1")),
          table
        )
        table <- dispatcher.dispatch(
          Member.Role.Player.Commands.PutDownCard(player3, Card("2")),
          table
        )
        table <- dispatcher.dispatch(
          Member.Role.Player.Commands.PutDownCard(player4, Card("1")),
          table
        )
        table <- dispatcher.dispatch(
          Member.Role.Player.Commands.ChangeCardOnTable(player4, Card("3")),
          table
        )
      } yield {
        assert(table.cards.map(c => c.suite).mkString(" ") == "* * *")

        table
      }

      val tableAfterShowDown = for {
        table <- tableAfterPlayed
        table <- dispatcher.dispatch(
          Member.Role.Facilitator.Commands.ShowDown(facilitator),
          table
        )
      } yield {
        assert(table.cards.map(c => c.suite).mkString(" ") == "1 2 3")

        table
      }

      for {
        table <- tableAfterShowDown
        table <- dispatcher.dispatch(
          Member.Role.Facilitator.Commands.CloseTable(facilitator),
          table
        )
      } {
        assert(table.cards.isEmpty)
      }
    }
  }
}
