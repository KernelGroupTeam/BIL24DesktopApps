package report.utils;

import java.io.*;

import org.apache.poi.openxml4j.opc.StreamHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 19.01.2018
 */
public final class StaticMethods {
  private StaticMethods() {
  }

  public static boolean isDigit(@NotNull String text) {
    for (char ch : text.toCharArray()) if (!Character.isDigit(ch)) return false;
    return true;
  }

  @NotNull
  public static String generateSign(@NotNull String userType, @NotNull String email) {
    return userType + ": " + email;
  }

  public static void fix() {
    String key = "javax.xml.transform.TransformerFactory";
    String oldValue = System.getProperty(key);
    System.setProperty(key, "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    StreamHelper.saveXmlInStream(null, new OutputStream() {
      @Override
      public void write(int b) throws IOException {
      }
    });
    if (oldValue == null) System.clearProperty(key);
    else System.setProperty(key, oldValue);
  }
}
