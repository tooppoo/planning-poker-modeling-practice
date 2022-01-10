package philomagi.dddcj.modeling.planning_poker.core.test_helper

import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table

object TestHelper {
  trait UseDummyTable {
    def tableId: Table.Id = Table.Id("dummy-table")
    def tableOwner: Attendance

    def table: Table = Table(tableId, tableOwner)
  }
  trait UseDummyTableOwner {
    def rolesOfOwner: Seq[Role] = Seq(Role.Facilitator)

    def tableOwner: Attendance = Attendance(
      Attendance.Id("dummy-owner-id"),
      Attendance.Name("dummy-owner-name"),
      rolesOfOwner
    )
  }
  trait UseDummyPlayer {
    def rolesOfPlayer: Seq[Role] = Seq(Role.Player)

    def player: Attendance = Attendance(
      Attendance.Id("dummy-player"),
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
