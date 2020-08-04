package co.thenets.brisk.interfaces;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 07/03/2016.
 */
public interface ConfirmationListener extends Serializable
{
    void onApprove();
    void onCancel();
}
