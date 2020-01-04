package teacher_leaves;

import javafx.scene.input.MouseEvent;
import model.Leave;

public interface LeavesCardListener {

    void onContextMenuRequested(Leave leave, MouseEvent event);

    void onCardClick(Leave leave);


}
