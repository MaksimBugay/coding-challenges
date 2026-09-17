package bmv;

import java.util.ArrayList;
import java.util.List;

public class Encryption {

  public static String encrypt(String input) {
    String s = input.replaceAll(" ", "");
    int l = s.length();
    int min = (int) Math.floor(Math.sqrt(l));
    int max = (int) Math.ceil(Math.sqrt(l));

    String[] chunks = splitIntoChunks(s, min);

    if (chunks.length > min || chunks.length * min < l) {
      chunks = splitIntoChunks(s, max);
    }

    List<String> result = transform(chunks);

    return String.join(" ", result);
  }

  private static List<String> transform(String[] chunks) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < chunks[0].length(); i++) {
      String tmp = "";
      for (String chunk : chunks) {
        if (i < chunk.length()) {
          tmp = tmp + chunk.charAt(i);
        }
      }
      result.add(tmp);
    }
    return result;
  }

  private static String[] splitIntoChunks(String s, int chunkLength) {
    String regExp = "(?<=\\G.{" + chunkLength + "})";
    return s.split(regExp);
  }
}
