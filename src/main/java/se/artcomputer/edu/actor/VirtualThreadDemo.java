package se.artcomputer.edu.actor;

public class VirtualThreadDemo {

    public static void main(String[] args) {
        System.out.println("Skapar en virtuell tråd.");
        Thread virtualThread = Thread.startVirtualThread(getViktor());
        System.out.println("Tråden är virtuell? " + virtualThread.isVirtual());
        // Vänta på att den virtuella tråden ska avslutas
        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            System.out.println("Virtuella tråde avbruten!");
        }

        System.out.println("Huvudtråden avslutas, kallad '" + Thread.currentThread().getName() + "'.");
    }

    private static Runnable getViktor() {
        return () -> {
            Thread.currentThread().setName("Viktor");
            System.out.println("Kör i en tråd '" + Thread.currentThread().getName() + "'.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Avbruten!");
            }
            System.out.println("Klar!");
        };
    }
}
