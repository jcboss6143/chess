package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public interface CommonAccessSQL {

    default int executeUpdateCore(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.executeUpdate();
        return 1;
    }

    @FunctionalInterface
    interface AccessMethod<R> { R apply(PreparedStatement preparedStatement) throws SQLException; }

    default <R> R sendStatement(String statement, String errorMessage, AuthAccessSQL.AccessMethod<R> accessMethod) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                return accessMethod.apply(preparedStatement);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("%s: %s", errorMessage, ex.getMessage()));
        }
    }
}
