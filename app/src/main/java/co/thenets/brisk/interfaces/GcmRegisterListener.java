package co.thenets.brisk.interfaces;

/**
 * Created by DAVID BELOOSESKY on 13/01/2015.
 */
public interface GcmRegisterListener
{
    void onGcmRegistrationCompleted(String regID);
    void onGcmRegistrationFailed();
}
