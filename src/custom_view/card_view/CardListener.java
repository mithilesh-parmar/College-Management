package custom_view.card_view;

import javafx.scene.input.MouseEvent;

public interface CardListener {

    void onCardClick();

    void onDeleteButtonClick();

    void onEditButtonClick();

    void onNotificationButtonClick();

    void onContextMenuRequested(MouseEvent event);

}
