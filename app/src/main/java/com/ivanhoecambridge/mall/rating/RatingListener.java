package com.ivanhoecambridge.mall.rating;

/**
 * Created by petar on 2017-11-08.
 */

public interface RatingListener {
    /**
     * Creates a rating dialog for the host activity.
     * @param dialogActionListener dialogActionListener to handle dialog actions for AppRatingManager.
     */
    void onCreateRatingDialog(DialogActionListener dialogActionListener);

    /**
     * Destroys the rating dialog once it's been shown.
     */
    void onDestroyRatingDialog();

    /**
     * Callback for AppRatingManager to handle any actions required when one of the dialog buttons is pressed.
     */
    interface DialogActionListener {
        /**
         * Callback when the user presses OK and is re-directed to the Play Store.
         */
        void onDialogOK();

        /**
         * Callback when the user presses LATER and the dialog is dismissed.
         */
        void onDialogDeferred();
    }
}
