package swm.wbj.asyncrum.global.utils;

public class UiAvatarService {

    public static String getProfileImageUrl(String fullname) {
        return "https://ui-avatars.com/api/?name=" + tokenize(fullname);
    }

    private static String tokenize(String fullname) {
        StringBuilder result = new StringBuilder("" + fullname.charAt(0));
        for (int i=1; i< fullname.length(); i++) {
            char c = fullname.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                result.append("+");
                result.append(c);
            }
            else if (c == ' ') {
                result.append("+");
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
