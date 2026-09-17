package bmv.prefix;

public class LongestCommonPrefix {

  public String longestCommonPrefix(String[] strs) {
    String first = strs[0];
    for (int characterIndex = 0; characterIndex < first.length(); characterIndex++) {
      char expected = first.charAt(characterIndex);
      for (int stringIndex = 1; stringIndex < strs.length; stringIndex++) {
        String current = strs[stringIndex];
        if (characterIndex >= current.length()
            || current.charAt(characterIndex) != expected) {
          return first.substring(0, characterIndex);
        }
      }
    }
    return first;
  }
}
