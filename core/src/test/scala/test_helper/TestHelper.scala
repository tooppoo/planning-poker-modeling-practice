package philomagi.dddcj.modeling.planning_poker.core
package test_helper

import domain.attendance.model.Attendance
import domain.role.model.Role
import domain.table.model.Table

object TestHelper {
  trait UseDummyTable {
    def tableId: Table.Id = Table.Id("a5aab3d1-0688-69ec-5bd9-a87f091086e2")
    def tableOwner: Attendance

    def table: Table = Table(tableId, tableOwner)
  }
  trait UseDummyTableOwner {
    def rolesOfOwner: Seq[Role] = Seq(Role.Facilitator)

    def tableOwner: Attendance = Attendance(
      Attendance.Id("ddfbe0b5-ef42-2914-29c4-8581c6145c74"),
      Attendance.Name("dummy-owner-name"),
      rolesOfOwner
    )
  }
  trait UseDummyPlayer {
    def rolesOfPlayer: Seq[Role] = Seq(Role.Player)

    def player: Attendance = Attendance(
      Attendance.Id("d69c9d26-d7ac-3142-69c0-af9deb953ea2"),
      Attendance.Name("dummy-player"),
      rolesOfPlayer
    )
  }
  trait AfterJoinToTable {
    def player: Attendance
    def table: Table

    def tableAfterJoin: Table = table.accept(player).fold(
      e => throw new Exception("failed to join because " + e.getMessage),
      t => t
    )
  }
}
