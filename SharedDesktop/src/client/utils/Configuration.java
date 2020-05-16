package client.utils;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.02.13
 *
 * @version 2.1.20170610
 */
public class Configuration {
  private final File file;
  private final File tempFile;
  private final String comment;
  private final String charsetName;
  private final SortedProperties props;

  public Configuration(File file) throws IOException {
    this(file, null);
  }

  public Configuration(File file, String comment) throws IOException {
    this(file, comment, null);
  }

  public Configuration(File file, String comment, String charsetName) throws IOException {
    this.file = file;
    this.tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
    this.comment = comment;
    this.charsetName = charsetName;
    props = new SortedProperties();

    Reader in;
    if (charsetName == null) in = new InputStreamReader(new FileInputStream(file));
    else in = new InputStreamReader(new FileInputStream(file), charsetName);
    try {
      props.load(in);
    } finally {
      in.close();
    }
  }

  public synchronized void flush() throws IOException {
    Writer out;
    if (charsetName == null) out = new OutputStreamWriter(new FileOutputStream(tempFile));
    else out = new OutputStreamWriter(new FileOutputStream(tempFile), charsetName);
    try {
      props.store(out, comment);
    } finally {
      out.close();
    }
    if (!file.delete()) throw new IOException("Cannot delete old configuration file: " + file.getAbsolutePath());
    if (!tempFile.renameTo(file))
      throw new IOException("Cannot rename temp configuration file " + tempFile.getAbsolutePath() + " to " + file.getAbsolutePath());
  }

  public synchronized Object setProperty(String key, String value) {
    return props.put(key, value);
  }

  public synchronized Object flushProperty(String key, String value) throws IOException {
    Object result = props.put(key, value);
    flush();
    return result;
  }

  public String getProperty(String key) {
    return props.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return props.getProperty(key, defaultValue);
  }

  public Enumeration<?> propertyNames() {
    return props.propertyNames();
  }

  public Set<String> stringPropertyNames() {
    return props.stringPropertyNames();
  }

  public static class SortedProperties extends Properties {
    @Override
    public Enumeration<Object> keys() {
      Enumeration<Object> e = super.keys();
      SortedEnumeration<Object> sorter = new SortedEnumeration<>();
      while (e.hasMoreElements()) {
        sorter.add(e.nextElement());
      }
      return sorter;
    }

    static class SortedEnumeration<E> implements Enumeration<E> {
      private final TreeSet<E> set = new TreeSet<>();
      private Iterator<E> it;

      public void add(E element) {
        set.add(element);
        it = set.iterator();
      }

      @Override
      public boolean hasMoreElements() {
        return it.hasNext();
      }

      @Override
      public E nextElement() {
        return it.next();
      }
    }
  }
}
