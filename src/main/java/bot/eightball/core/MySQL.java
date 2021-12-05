package bot.eightball.core;

import bot.eightball.utilities.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    public Connection connection;

    private Connection makeConnection(Tokens.MySQL authentication) throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(String.format(
                "jdbc:mysql://%s:%s/%s", authentication.host, authentication.port, authentication.db
        ), authentication.user, authentication.password);
    }

    public MySQL(Tokens.MySQL authentication) {
        try {
            this.connection = makeConnection(authentication);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Logger.ERROR("Failed to initialize database connection");
        }

    }

}
