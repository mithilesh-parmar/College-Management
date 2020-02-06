package custom_view.loading_combobox.class_section_combobox;

import model.ClassItem;
import model.Section;

public interface ClassSectionListener {
    void onSectionSelected(Section section);

    void onClassSelected(ClassItem classItem);
}
