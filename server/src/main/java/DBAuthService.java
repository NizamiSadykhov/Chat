import java.sql.*;
import java.util.HashMap;

public class DBAuthService implements AuthService {
    private HashMap<String, String> users = new HashMap<>();

    public DBAuthService() {
        users.put("Vasya", "password");
        users.put("Toto", "pass");
        users.put("Foo", "foopass");
    }


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        if (users.containsKey(login) && users.get(login).equals(password)) {
            System.out.println("return " + login);
            return login;
        }
        return null;

    }
//    private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/";
//    private static final String DB_USER = "postgres";
//    private static final String DB_PASSWORD = "postgres";
//    private static Connection sConnection;
//
//    static {
//        try {
//            sConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getNicknameByLoginAndPassword(String login, String password) {
//        try (PreparedStatement stm =
//                     sConnection.prepareStatement("SELECT * from chat_users WHERE login='" + login + "' AND pass='" + password + "'")) {
//
//            ResultSet resultSet = stm.executeQuery();
//            if (resultSet.next()) {
//                return login;
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return null;
//    }
}
