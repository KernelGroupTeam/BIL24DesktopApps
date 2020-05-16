package client.component.summary;

import java.awt.*;
import java.net.URL;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import org.jdesktop.swingx.JXEditorPane;

public class JXFiltrableEditorPane extends JXEditorPane {
  private String origText;

  public JXFiltrableEditorPane() {
    super();
  }

  private JXFiltrableEditorPane(String url) { }
  private JXFiltrableEditorPane(String type, String text) { }
  private JXFiltrableEditorPane(URL initialPage) { }

  @Override
  public void setText(String t) {
    origText = t;
    super.setText(t);
  }

  public void deleteLines(String text) {
    text = text.toLowerCase();
    getHighlighter().removeAllHighlights();
    String[] lines = origText.split("\n");
    StringBuilder str = new StringBuilder();
    for (String line : lines) {
      if (!line.toLowerCase().contains(text)) str.append(line).append("\n");
    }
    super.setText(str.toString());
  }

  public void leaveLines(String text) {
    text = text.toLowerCase();
    getHighlighter().removeAllHighlights();
    String[] lines = origText.split("\n");
    StringBuilder str = new StringBuilder();
    for (String line : lines) {
      if (line.toLowerCase().contains(text)) str.append(line).append("\n");
    }
    super.setText(str.toString());
  }

  public void highlightLines(String text, Color color) throws BadLocationException {
    text = text.toLowerCase();
    getHighlighter().removeAllHighlights();
    String str = getDocument().getText(0, getDocument().getLength()).toLowerCase();
    DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(color);
    String[] lines = getText().split("\n");
    int lastPos = 0;
    for (int i=0; i<lines.length; i++) {
      if (lines[i].toLowerCase().contains(text)) {
        lines[i] = lines[i].replaceAll("\r", "");
        int pos = str.indexOf(lines[i].toLowerCase(), lastPos);
        if (pos > -1) {
          getHighlighter().addHighlight(pos, pos+lines[i].length(), highlightPainter);
          lastPos = pos;
        }
      }
    }
  }

  public void revertLines() {
    super.setText(origText);
    getHighlighter().removeAllHighlights();
  }
}
