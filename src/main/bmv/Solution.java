package bmv;

import java.util.Stack;
import java.util.stream.Collectors;

public class Solution {

  private final char[] alphabet = new char[] {'A', 'B', 'C'};

  public String solution(String s) {
    Stack<Character> buf = new Stack<>();
    for (char c : s.toCharArray()) {
      if (buf.isEmpty()) {
        buf.push(c);
      } else {
        if (buf.peek().equals(c)) {
          buf.pop();
        } else {
          buf.push(c);
        }
      }
    }
    return buf.stream().map(String::valueOf).collect(Collectors.joining(""));
  }
}
