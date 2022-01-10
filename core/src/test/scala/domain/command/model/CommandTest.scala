package philomagi.dddcj.modeling.planning_poker.core
package domain.command.model

import domain.attendance.model.Attendance
import domain.card.model.Card
import domain.role.model.Role
import domain.table.model.Table
import test_helper.TestHelper.{UseDummyTable, UseDummyTableOwner}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class CommandTest extends AnyFunSpec with TableDrivenPropertyChecks {
  describe(Command.Commands.SetUpNewTable.toString()) {
    val testCases = Table(
      ("所有ロール", "expected"),
      (Seq.empty[Role], Expected.Fail),
      (Seq(Role.NewComer), Expected.Fail),
      (Seq(Role.Audience), Expected.Fail),
      (Seq(Role.Facilitator), Expected.Success),
      (Seq(Role.Player), Expected.Fail),
    )

    runTestsForCommand(testCases) { (roles, t) =>
      val ctx = new UseActor {
        override def actorsRoles: Seq[Role] = roles
      }

      (Command.Commands.SetUpNewTable(ctx.actor), t)
    }
  }

  describe(Command.Commands.ShowDown.toString()) {
    val testCases = Table(
      ("所有ロール", "expected"),
      (Seq.empty[Role], Expected.Fail),
      (Seq(Role.NewComer), Expected.Fail),
      (Seq(Role.Audience), Expected.Fail),
      (Seq(Role.Facilitator), Expected.Success),
      (Seq(Role.Player), Expected.Fail),
    )

    runTestsForCommand(testCases) { (roles, t) =>
      val ctx = new UseActor {
        override def actorsRoles: Seq[Role] = roles
      }

      (Command.Commands.ShowDown(ctx.actor), t)
    }
  }
  describe(Command.Commands.CloseTable.toString()) {
    val testCases = Table(
      ("所有ロール", "expected"),
      (Seq.empty[Role], Expected.Fail),
      (Seq(Role.NewComer), Expected.Fail),
      (Seq(Role.Audience), Expected.Fail),
      (Seq(Role.Facilitator), Expected.Success),
      (Seq(Role.Player), Expected.Fail),
    )

    runTestsForCommand(testCases) { (roles, t) =>
      val ctx = new UseActor {
        override def actorsRoles: Seq[Role] = roles
      }

      (Command.Commands.CloseTable(ctx.actor), t)
    }
  }
  describe(Command.Commands.Join.toString()) {
    val testCases: TableFor2[Seq[Role], Expected] = Table(
      ("所有ロール", "expected"),
      (Seq.empty[Role], Expected.Success),
      (Seq(Role.NewComer), Expected.Success),
      (Seq(Role.Audience), Expected.Success),
      (Seq(Role.Facilitator), Expected.Success),
      (Seq(Role.Player), Expected.Success),
    )

    runTestsForCommand(testCases) { (roles, t) =>
      val ctx = new UseActor {
        override def actorsRoles: Seq[Role] = roles
      }

      (Command.Commands.Join(ctx.actor), t)
    }
  }
  describe(Command.Commands.PutDownCard.toString()) {
    val testCases: TableFor2[Seq[Role], Expected] = Table(
      ("所有ロール", "expected"),
      (Seq.empty[Role], Expected.Fail),
      (Seq(Role.NewComer), Expected.Fail),
      (Seq(Role.Audience), Expected.Fail),
      (Seq(Role.Facilitator), Expected.Fail),
      (Seq(Role.Player), Expected.Success),
    )

    runTestsForCommand(testCases) { (roles, table) =>
      val ctx = new UseActor {
        override def actorsRoles: Seq[Role] = roles
      }

      (
        Command.Commands.PutDownCard(ctx.actor, Card(Card.Suite("4"))),
        table.accept(ctx.actor).getOrElse(table) // 事前参加必須
      )
    }
  }


  private def runTestsForCommand(testCases: TableFor2[Seq[Role], Expected])
                                (createSut: (Seq[Role], Table) => (Command, Table)): Unit = {
    forAll(testCases) { (roles, expected) =>
      describe(s"ロール $roles を所有している場合") {
        it(s"should $expected to run command") {
          new UseDummyTable with UseDummyTableOwner {
            val (sut, t) = createSut(roles, table)

            //noinspection SimplifyBoolean
            assert(expected.evaluate(sut.run(t)) == true)
          }
        }
      }
    }
  }

  trait UseActor {
    def actorsRoles: Seq[Role]

    def actor: Attendance = Attendance(
      Attendance.Id("69e2d3bc-0f83-33ac-972d-05f46bfa03dd"),
      Attendance.Name("dummy-actor"),
      actorsRoles
    )
  }

  trait Expected {
    def evaluate(r: Either[Exception, Table]): Boolean
  }
  object Expected {
    object Success extends Expected {
      override def evaluate(r: Either[Exception, Table]): Boolean = r.isRight
      override def toString: String = "Success"
    }
    object Fail extends Expected {
      override def evaluate(r: Either[Exception, Table]): Boolean = r.isLeft
      override def toString: String = "Fail"
    }
  }
}
