package custom_view.card_view;

import model.Leave;

public interface LeaveCardListener {
    void onClick(Leave leave);

    void onAcceptAction(Leave leave);

    void onDeclineAction(Leave leave);
}
