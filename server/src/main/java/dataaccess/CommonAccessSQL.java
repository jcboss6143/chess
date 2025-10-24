package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public interface CommonAccessSQL {

    default int executeUpdateCore(PreparedStatement preparedStatement, int filler) throws SQLException {
        preparedStatement.executeUpdate();
        return filler;
    }

    @FunctionalInterface
    interface AccessMethod<R, T> { R apply(PreparedStatement preparedStatement, T t) throws SQLException; }

    default <R, T> R sendStatement(String statement, String errorMessage, AuthAccessSQL.AccessMethod<R, T> accessMethod, T t) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                return accessMethod.apply(preparedStatement, t);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("%s: %s", errorMessage, ex.getMessage()));
        }
    }
}
