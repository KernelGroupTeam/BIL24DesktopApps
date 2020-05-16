package client.utils;

import java.util.ListResourceBundle;
import javax.swing.*;

import com.sun.java.swing.plaf.windows.resources.windows;
import com.sun.swing.internal.plaf.basic.resources.basic;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 16.06.16
 *
 * @version 1.0.003
 */
public class L10n {

  private L10n() {
  }

  @SuppressWarnings("unused")
  private static void listOut(String substring) {
    ListResourceBundle basic = new basic();
    for (String key : basic.keySet()) {
      if (key != null && key.contains(substring)) {
        System.out.println(key + " = " + basic.getString(key));
      }
    }
    ListResourceBundle windows = new windows();
    for (String key : windows.keySet()) {
      if (key != null && key.contains(substring)) {
        System.out.println(key + " = " + windows.getString(key));
      }
    }
  }

  public static void localize() {
    UIManager.put("JXTable.column.horizontalScroll", "Горизонтальная прокрутка");
    UIManager.put("JXTable.column.packAll", "Уплотнить все столбцы");
    UIManager.put("JXTable.column.packSelected", "Уплотнить выбранные столбцы");
    UIManager.put("JXDatePicker.linkFormat", "Сегодня {0,date, dd MMMM yyyy}");
    UIManager.put("XDialog.close", "Закрыть");

    UIManager.put("Search.matchCase", "с учетом регистра");
    UIManager.put("Search.wrapSearch", "искать с начала");
    UIManager.put("Search.backwardsSearch", "обратный поиск");
    UIManager.put("Search.match", "Найти");
    UIManager.put("Search.close", "Закрыть");
    UIManager.put("Search.searchFieldLabel", "Найти");
    UIManager.put("Search.searchTitle", "Найти");
    UIManager.put("Search.notFound", "Фраза не найдена");

    UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
    UIManager.put("FileChooser.lookInLabelText", "Папка:");
    UIManager.put("FileChooser.saveInLabelText", "Папка:");
    UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
    UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла:");
    UIManager.put("FileChooser.openButtonText", "Открыть");
    UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
    UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
    UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть");
    UIManager.put("FileChooser.saveButtonText", "Сохранить");
    UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
    UIManager.put("FileChooser.cancelButtonText", "Отмена");
    UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
    UIManager.put("FileChooser.updateButtonText", "Обновить");
    UIManager.put("FileChooser.updateButtonToolTipText", "Обновить");
    UIManager.put("FileChooser.refreshActionLabelText", "Обновить");
    UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
    UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
    UIManager.put("FileChooser.newFolderAccessibleName", "Создать папку");
    UIManager.put("FileChooser.newFolderDialogText", "Создать папку");
    UIManager.put("FileChooser.newFolderActionLabelText", "Создать папку");
    UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
    UIManager.put("FileChooser.listViewButtonAccessibleName", "Список");
    UIManager.put("FileChooser.listViewActionLabelText", "Список");
    UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
    UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Таблица");
    UIManager.put("FileChooser.detailsViewActionLabelText", "Таблица");
    UIManager.put("FileChooser.viewMenuLabelText", "Вид");
    UIManager.put("FileChooser.viewMenuButtonToolTipText", "Меню \"Вид\"");
    UIManager.put("FileChooser.win32.newFolder", "Новая папка");
    UIManager.put("FileChooser.win32.newFolder.subsequent", "Новая папка ({0})");

    UIManager.put("OptionPane.yesButtonText", "Да");
    UIManager.put("OptionPane.noButtonText", "Нет");
    UIManager.put("OptionPane.cancelButtonText", "Отмена");
  }

  public static String pluralForm(int num, String form1, String form2, String form5) {
    return new PluralForm(form1, form2, form5).form(num);
  }

  public static String pluralVal(int num, String form1, String form2, String form5) {
    return new PluralForm(form1, form2, form5).val(num);
  }

  public static class PluralForm {
    private final String form1;
    private final String form2;
    private final String form5;
    private final String form0;

    //1 место, 2 места, 5 мест
    public PluralForm(String form1, String form2, String form5) {
      this(form1, form2, form5, form5);
    }

    //1 место, 2 места, 5 мест, 0 мест
    public PluralForm(String form1, String form2, String form5, String form0) {
      this.form1 = form1;
      this.form2 = form2;
      this.form5 = form5;
      this.form0 = form0;
    }

    public String form(int num) {
      if (num == 0) return form0;
      int nn = Math.abs(num) % 100;
      if (nn > 10 && nn < 20) return form5;
      int n = nn % 10;
      if (n > 1 && n < 5) return form2;
      if (n == 1) return form1;
      return form5;
    }

    public String val(int num) {
      return num + " " + form(num);
    }
  }
}
