package philomagi.dddcj.modeling.planning_poker
package scenario

import card.Card
import command.Command
import member.Member
import table.Table

import org.scalatest.funspec.AnyFunSpec

class ScenarioTest extends AnyFunSpec {
  describe("プランニングポーカー実施") {
    it("テーブル準備 ~ メンバー招待 ~ ポーカープレイ ~ 解散") {
      val facilitator = Member(
        Member.Id("1"),
        Member.Name("John Doe"),
        List(Member.Role.Facilitator, Member.Role.Player)
      )

      var table = Command.dispatch(
        Member.Role.Facilitator.Commands.SetUpNewTable,
        Table(Table.Id("table"), facilitator)
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

      // プレイヤー参加
      table = Command.dispatch(
        Member.Role.NewComer.Commands.Join(player2),
        table
      )
      table = Command.dispatch(
        Member.Role.NewComer.Commands.Join(player3),
        table
      )
      table = Command.dispatch(
        Member.Role.NewComer.Commands.Join(player4),
        table
      )

      table = Command.dispatch(
        Member.Role.Player.Commands.PutDownCard(facilitator, Card("1")),
        table
      )
      table = Command.dispatch(
        Member.Role.Player.Commands.PutDownCard(player3, Card("2")),
        table
      )
      table = Command.dispatch(
        Member.Role.Player.Commands.PutDownCard(player4, Card("1")),
        table
      )
      table = Command.dispatch(
        Member.Role.Player.Commands.ChangeCardOnTable(player4, Card("3")),
        table
      )

      assert(table.cards.map(c => c.suite).mkString(" ") == "* * *")

      table = Command.dispatch(
        Member.Role.Facilitator.Commands.ShowDown,
        table
      )

      assert(table.cards.map(c => c.suite).mkString(" ") == "1 2 3")

      table = Command.dispatch(
        Member.Role.Facilitator.Commands.CloseTable,
        table
      )

      assert(table.cards.isEmpty)
    }
  }
}
