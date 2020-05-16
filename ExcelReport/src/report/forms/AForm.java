package report.forms;

import java.awt.*;
import java.io.*;

import excel.enums.EStyle;
import excel.wraps.WrapSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.Header;

/**
 * Created by Inventor on 13.11.2017
 */
public abstract class AForm<Data> {
  @NotNull
  private final EForm form;//Форма
  @Nullable
  private final String sign;//Подпись
  @NotNull
  private final XSSFWorkbook book = new XSSFWorkbook();//Книга
  @NotNull
  private final WrapSheet sheet;//Страница
  @NotNull
  private final Header header;//Шапка
  private Data data;//Конечный объект для заполнения отчета данными
  @Nullable
  private File file;//Файл отчета
  @Nullable
  private byte[] bytes;//Байт массив отчета

  protected AForm(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign) {
    this.form = form;
    this.sign = sign;
    this.sheet = new WrapSheet(book.createSheet(sheetName == null ? form.getName() : sheetName));
    this.header = new Header(sheet);
  }

  protected abstract void fillSheet(@NotNull WrapSheet sheet, @NotNull Data data);

  @NotNull
  public final EForm getForm() {
    return form;
  }

  @NotNull
  public final XSSFWorkbook getBook() {
    return book;
  }

  @Nullable
  public final byte[] getBytes() {
    return bytes;
  }

  public final void build() throws ValidationException {
    validate();

    fillHeader();
    header.build();

    fillSheet(sheet, data);

    if (sign != null) {
      sheet.incRowCurrentIndex();
      sheet.incRowCurrentIndex();
      sheet.createRow().createCell(sign, EStyle.NORMAL);
    }
  }

  protected void validate() throws ValidationException {
    if (data == null) throw ValidationException.absent("Конечный объект для заполнения отчета данными");
  }

  protected void fillHeader() {
    header.add(EHeader.NAME, form.getName());
  }

  @NotNull
  protected final Header getHeader() {
    return header;
  }

  protected final void setData(@NotNull Data data) {
    this.data = data;
  }

  @NotNull
  protected final Data getData() {
    return data;
  }

  public final void writeToFile() throws IOException {
    if (file != null) return;
    file = File.createTempFile(form.getName(), ".xlsx");
    file.deleteOnExit();
    try (FileOutputStream fos = new FileOutputStream(file)) {
      book.write(fos);
    }
    book.close();
  }

  public final void writeToBytes() throws IOException {
    if (bytes != null) return;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      book.write(baos);
      bytes = baos.toByteArray();
    }
    book.close();
  }

  public final void open() throws IOException {
    if (file == null) throw new IOException("File excel doesn't exist");
    Thread thread = new Thread("Open " + file.getName()) {
      @Override
      public void run() {
        try {
          Desktop.getDesktop().open(file);
        } catch (IOException ignored) {
        }
      }
    };
    thread.setDaemon(true);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }
}
