public class PostLoginState extends UniversalState {

    public PostLoginState(String userName) {
        displayName = userName;
        continueLoop = true;
    }

    @Override
    String evaluateCommand(String input, String[] params) {
        return "implement";
    }

    public void help() {}

    public void exit() {}

}
