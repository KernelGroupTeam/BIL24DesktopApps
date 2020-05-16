package client.utils;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * PrintStream с перенаправлением в JTextArea, JTextPane либо с перекодированием в нужную кодировку
 *
 * @author Maksim Ponomarev
 * @version 1.0.20151210
 */

public class ConsolePrintStream extends PrintStream {
  private final byte[] oneByte = new byte[1]; //array for write(int val);
  private final String encoding;
  private final Appender appender;
  private final boolean errStream;

  public ConsolePrintStream(Appender appender, boolean errStream) {
    this(new NullOutputStream(), appender, errStream);
  }

  public ConsolePrintStream(OutputStream outputStream, Appender appender, boolean errStream) {
    super(outputStream);
    this.encoding = null;
    this.appender = appender;
    this.errStream = errStream;
  }

  public ConsolePrintStream(OutputStream outputStream, String encoding) throws UnsupportedEncodingException {
    this(outputStream, encoding, null, false);
  }

  public ConsolePrintStream(OutputStream outputStream, String encoding, Appender appender, boolean errStream) throws UnsupportedEncodingException {
    super(outputStream, false, encoding);
    this.encoding = encoding;
    this.appender = appender;
    this.errStream = errStream;
  }

  /**
   * Clear the current console text area.
   */
  public synchronized void clear() {
    if (appender != null) appender.clear();
  }

  @Override
  public synchronized void write(int val) {
    super.write(val);
    oneByte[0] = (byte) val;
    write(oneByte, 0, 1);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public synchronized void write(byte[] bytes) {
    try {
      super.write(bytes);
    } catch (IOException ignored) {
    }
    write(bytes, 0, bytes.length);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public synchronized void write(byte[] bytes, int off, int len) {
    super.write(bytes, off, len);
    if (appender != null) appender.append(bytesToString(bytes, off, len, encoding), errStream);
  }

  private static String bytesToString(byte[] bytes, int str, int len, String encoding) {
    if (encoding == null) return new String(bytes, str, len);
    try {
      return new String(bytes, str, len, encoding);
    } catch (UnsupportedEncodingException thr) {
      return new String(bytes, str, len);
    }
  }

  public interface Appender {

    void append(String val, boolean errStream);

    void clear();
  }

  public static class TextAreaAppender implements Appender, Runnable {
    private static final String EOL1 = "\n";
    private static final String EOL2 = System.getProperty("line.separator", EOL1);
    private final JTextArea textArea;
    private final int maxLines; //maximum lines allowed in text area
    private final LinkedList<Integer> lengths = new LinkedList<>(); //length of lines within text area
    private final List<String> values = new ArrayList<>(); //values waiting to be appended

    private int curLength; //length of current line
    private boolean clear;
    private boolean queue;

    public TextAreaAppender(JTextArea textArea) {
      this(textArea, 100);
    }

    public TextAreaAppender(JTextArea textArea, int maxLines) {
      if (maxLines < 1) throw new IllegalArgumentException("Maximum lines must be positive");
      this.textArea = textArea;
      this.maxLines = maxLines;

      curLength = 0;
      clear = false;
      queue = true;
    }

    @Override
    public synchronized void append(String val, boolean errStream) {
      values.add(val);
      if (queue) {
        queue = false;
        EventQueue.invokeLater(this);
      }
    }

    @Override
    public synchronized void clear() {
      clear = true;
      curLength = 0;
      lengths.clear();
      values.clear();
      if (queue) {
        queue = false;
        EventQueue.invokeLater(this);
      }
    }

    // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
    @Override
    public synchronized void run() {
      if (clear) {
        textArea.setText("");
      }
      for (String val : values) {
        curLength += val.length();
        if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
          if (lengths.size() >= maxLines) {
            textArea.replaceRange("", 0, lengths.removeFirst());
          }
          lengths.addLast(curLength);
          curLength = 0;
        }
        textArea.append(val);
      }
      values.clear();
      clear = false;
      queue = true;
    }
  }

  public static class TextPaneAppender implements Appender, Runnable {
    private static final String EOL1 = "\n";
    private static final String EOL2 = System.getProperty("line.separator", EOL1);
    private final JTextPane textPane;
    private final Style style;
    private final Style errStyle;
    private final int maxLines; //maximum lines allowed in text area
    private final LinkedList<Integer> lengths = new LinkedList<>(); //length of lines within text area
    private final List<String> values = new ArrayList<>(); //values waiting to be appended
    private final List<Style> styles = new ArrayList<>(); //styles waiting to be appended

    private int curLength; //length of current line
    private boolean clear;
    private boolean queue;

    public TextPaneAppender(JTextPane textPane, Style style, Style errStyle) {
      this(textPane, style, errStyle, 100);
    }

    public TextPaneAppender(JTextPane textPane, Style style, Style errStyle, int maxLines) {
      if (maxLines < 1) throw new IllegalArgumentException("Maximum lines must be positive");
      this.textPane = textPane;
      this.style = style;
      this.errStyle = errStyle;
      this.maxLines = maxLines;

      curLength = 0;
      clear = false;
      queue = true;
    }

    @Override
    public synchronized void append(String val, boolean errStream) {
      values.add(val);
      styles.add(errStream ? errStyle : style);
      if (queue) {
        queue = false;
        EventQueue.invokeLater(this);
      }
    }

    @Override
    public synchronized void clear() {
      clear = true;
      curLength = 0;
      lengths.clear();
      values.clear();
      styles.clear();
      if (queue) {
        queue = false;
        EventQueue.invokeLater(this);
      }
    }

    // MUST BE THE ONLY METHOD THAT TOUCHES textPane!
    @Override
    public synchronized void run() {
      StyledDocument doc = textPane.getStyledDocument();
      if (clear) {
        textPane.setText("");
      }
      for (int i = 0; i < values.size(); i++) {
        String val = values.get(i);
        curLength += val.length();
        if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
          if (lengths.size() >= maxLines) {
            try {
              doc.remove(0, lengths.removeFirst());
            } catch (BadLocationException ignored) {
            }
          }
          lengths.addLast(curLength);
          curLength = 0;
        }
        try {
          doc.insertString(doc.getLength(), val, styles.get(i));
        } catch (BadLocationException ignored) {
        }
      }
      values.clear();
      styles.clear();
      clear = false;
      queue = true;
    }
  }

  @SuppressWarnings("NullableProblems")
  private static class NullOutputStream extends OutputStream {
    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte[] b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }
  }
}
