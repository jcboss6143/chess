public class PreLoginState extends UniversalState {

    public PreLoginState() {
    }

    @Override
    String evaluateCommand(String cmd, String[] params) {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> quit();
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String register(String[] params) {
        return "implement";
    }

    private String login(String[] params) {
        // login and call PostLoginState
        return "implement";
    }

    private String quit() {
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
