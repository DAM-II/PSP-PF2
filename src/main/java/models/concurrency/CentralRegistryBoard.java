package models.concurrency;

import java.util.concurrent.Semaphore;

public class CentralRegistryBoard {
    private final Semaphore registryMutex;
    private int signedCertificates;

    public CentralRegistryBoard(){
        this.registryMutex = new Semaphore(1);
        this.signedCertificates = 0;
    }

    public void signCertificate(String robotName) throws InterruptedException{
        registryMutex.acquire();
        try{
            // Simulate signing quality certificate
            Thread.sleep(200);
            signedCertificates++;
        } finally {
            registryMutex.release();
        }
    }

    public int getSignedCertificates(){
        return signedCertificates;
    }
}
