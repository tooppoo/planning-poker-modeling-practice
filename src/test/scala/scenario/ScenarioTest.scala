package philomagi.dddcj.modeling.planning_poker
package scenario

import domain.model.card.Card
import domain.model.command.Command
import domain.model.command.Command.Commands
import domain.model.member.Member
import domain.model.role.Role
import domain.model.table.Table

import org.scalatest.funspec.AnyFunSpec

class ScenarioTest extends AnyFunSpec {

  describe("プランニングポーカー実施") {
    val dispatcher = Command.Dispatcher.NoPersistenceDispatcher

    it("テーブル準備 ~ メンバー招待 ~ ポーカープレイ ~ 解散") {
      val facilitator = Member(
        Member.Id("1"),
        Member.Name("John Doe"),
        List(Role.Facilitator, Role.Player)
      )

      val player2 = Member(
        Member.Id("2"),
        Member.Name("Jane Doe"),
        List(Role.Audience)
      )
      val player3 = Member(
        Member.Id("3"),
        Member.Name("Ulick Norman Owen"),
        List(Role.Audience, Role.Player)
      )
      val player4 = Member(
        Member.Id("4"),
        Member.Name("Una Nancy Owen"),
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
        assert(table.members.isEmpty)
      }
    }
  }
}
