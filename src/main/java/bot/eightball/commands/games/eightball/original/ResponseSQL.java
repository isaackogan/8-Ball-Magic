package bot.eightball.commands.games.eightball.original;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResponseSQL {

    public static List<ResponseEntry> getResponseMessageEntries(String tableName, Long guildId) {
        List<ResponseEntry> responses = new ArrayList<>();
        MySQL mySQL = new MySQL(MagicBalls.tokens.mysql);

        try {
            PreparedStatement preparedStatement = mySQL.connection.prepareStatement(
                    String.format("SELECT id, message from %s where guild_id=%s", tableName, guildId)
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                responses.add(new ResponseEntry(resultSet.getLong(1), resultSet.getString(2)));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            mySQL.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responses;
    }

    public static List<String> getResponseMessages(String tableName, Long guildId) {
        List<String> responses = new ArrayList<>();
        MySQL mySQL = new MySQL(MagicBalls.tokens.mysql);

        try {
            PreparedStatement preparedStatement = mySQL.connection.prepareStatement(
                    String.format("SELECT message from %s where guild_id=%s", tableName, guildId)
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                responses.add(resultSet.getString(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            mySQL.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responses;
    }

    public static Boolean deleteResponseMessage(String tableName, Long guildId, Long id) {
        MySQL mySQL = new MySQL(MagicBalls.tokens.mysql);

        try {
            PreparedStatement preparedStatement = mySQL.connection.prepareStatement(
                    String.format("DELETE FROM %s WHERE guild_id=%s AND id=%s", tableName, guildId, id)
            );

            boolean success = preparedStatement.executeUpdate() > 0;
            mySQL.connection.close();
            return success;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            mySQL.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean putResponseMessage(String tableName, Long guildId, String message) {
        MySQL mySQL = new MySQL(MagicBalls.tokens.mysql);
        message = message.replaceAll("\"", "'").replaceAll(";", ":").replace("\\", "/");

        try {
            PreparedStatement preparedStatement = mySQL.connection.prepareStatement(
                    String.format("INSERT INTO %s (guild_id, message) VALUES (%s, \"%s\")", tableName, guildId, message)
            );
            preparedStatement.execute();
            mySQL.connection.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();

            try {
                mySQL.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

    }
}
