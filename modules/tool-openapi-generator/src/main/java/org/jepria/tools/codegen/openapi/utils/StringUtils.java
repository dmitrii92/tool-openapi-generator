package org.jepria.tools.codegen.openapi.utils;

import com.google.common.base.Strings;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  /**
   * Camelize name (parameter, property, method, etc) with upper case for first letter copied from Twitter elephant bird https://github.com/twitter/elephant-bird/blob/master/core/src/main/java/com/twitter/elephantbird/util/Strings.java
   *
   * @param word string to be camelize
   * @return camelized string
   */
  public static String camelize(String word) {
    return camelize(word, false);
  }

  /**
   * Camelize name (parameter, property, method, etc)
   *
   * @param word                 string to be camelize
   * @param lowercaseFirstLetter lower case for first letter if set to true
   * @return camelized string
   */
  public static String camelize(String word, boolean lowercaseFirstLetter) {
    // Replace all slashes with dots (package separator)
    Pattern p = Pattern.compile("\\/(.?)");
    Matcher m = p.matcher(word);
    while (m.find()) {
      word = m.replaceFirst("." + m.group(1)/*.toUpperCase()*/); // FIXME: a parameter should not be assigned. Also declare the methods parameters as 'final'.
      m    = p.matcher(word);
    }

    // case out dots
    String[]      parts = word.split("\\.");
    StringBuilder f     = new StringBuilder();
    for (String z : parts) {
      if (z.length() > 0) {
        f.append(Character.toUpperCase(z.charAt(0))).append(z.substring(1));
      }
    }
    word = f.toString();

    m = p.matcher(word);
    while (m.find()) {
      word = m.replaceFirst("" + Character.toUpperCase(m.group(1).charAt(0)) + m.group(1).substring(1)/*.toUpperCase()*/);
      m    = p.matcher(word);
    }

    // Uppercase the class name.
    p = Pattern.compile("(\\.?)(\\w)([^\\.]*)$");
    m = p.matcher(word);
    if (m.find()) {
      String rep = m.group(1) + m.group(2).toUpperCase(Locale.ROOT) + m.group(3);
      rep  = rep.replaceAll("\\$", "\\\\\\$");
      word = m.replaceAll(rep);
    }

    // Remove all underscores (underscore_case to camelCase)
    p = Pattern.compile("(_)(.)");
    m = p.matcher(word);
    while (m.find()) {
      String original  = m.group(2);
      String upperCase = original.toUpperCase(Locale.ROOT);
      if (original.equals(upperCase)) {
        word = word.replaceFirst("_", "");
      } else {
        word = m.replaceFirst(upperCase);
      }
      m = p.matcher(word);
    }

    // Remove all hyphens (hyphen-case to camelCase)
    p = Pattern.compile("(-)(.)");
    m = p.matcher(word);
    while (m.find()) {
      word = m.replaceFirst(m.group(2).toUpperCase(Locale.ROOT));
      m    = p.matcher(word);
    }

    if (lowercaseFirstLetter && word.length() > 0) {
      int  i      = 0;
      char charAt = word.charAt(i);
      while (i + 1 < word.length() && !((charAt >= 'a' && charAt <= 'z') || (charAt >= 'A' && charAt <= 'Z'))) {
        i      = i + 1;
        charAt = word.charAt(i);
      }
      i    = i + 1;
      word = word.substring(0, i).toLowerCase(Locale.ROOT) + word.substring(i);
    }

    // remove all underscore
    word = word.replaceAll("_", "");

    return word;
  }

  public static String sanitizeName(String name) {
    name = name.trim(); // FIXME: a parameter should not be assigned. Also declare the methods parameters as 'final'.
    name = name.replaceAll("[^a-zA-Z0-9_\\.]", "_");
    if (Strings.isNullOrEmpty(name)) {
      return "invalidPackageName";
    }
    return name;
  }
}