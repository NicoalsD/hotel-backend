package com.hotel.reservations.repository.postgres;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomStatus;
import com.hotel.reservations.model.RoomType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convierte una fila de la tabla {@code rooms} en una {@link Room} del dominio.
 */
class RoomRowMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room(
                rs.getLong("id"),
                rs.getString("number"),
                RoomType.valueOf(rs.getString("type")),
                rs.getInt("floor"),
                rs.getString("description"));
        room.restoreStatus(RoomStatus.valueOf(rs.getString("status")));
        return room;
    }
}
