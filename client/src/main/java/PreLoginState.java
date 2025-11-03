public class PreLoginState extends UniversalState {
    public PreLoginState() {
        displayName = "[LOGGED_OUT]";
        continueLoop = true;
    }

    @Override
    String evaluateCommand(String input, String[] params) {
        return "implement";
    }

    public void help() {}

    public void exit() {}
}
