public class PostLoginState extends UniversalState {
    String authToken;

    public PostLoginState(String userName, String token) {
        displayName = userName;
        authToken = token;
    }

    @Override
    String evaluateCommand(String cmd, String[] params) {
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String create(String[] params) {
        return "implement";
    }

    private String list() {
        return "implement";
    }

    private String join(String[] params) {
        // login and call PostLoginState
        return "implement";
    }

    private String observe(String[] params) {
        // login and call PostLoginState
        return "implement";
    }

    private String logout() {
        continueLoop = false;
        return "Exiting...";
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - create an account and login
                login <USERNAME> <PASSWORD> - login to your account
                quit - go back to your shell
                help - list possible commands
                """;
    }

}
