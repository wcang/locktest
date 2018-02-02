package com.wcang;


public class Main {
    private static int  counter = 0;

    /**
     * This demo function is faulty because the synchronized block is holding on different objects
     */
    private static void faulty_synchronization_loop() {
        counter = 0;
        int i;
        System.out.println("faulty synchronization");
        for (i = 0; i < 10000; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        counter--;
                    }
                }
            });

            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        counter++;
                    }
                }
            });

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                System.err.println("Threads waiting interrupted " + e.getLocalizedMessage());
            }

            if (counter != 0) {
                break;
            }
        }

        if (counter == 0) {
            System.out.println("No faulty synchronization");
        }
        else {
            System.out.println("Faulty counter is " + counter + " at loop " + i);
        }
    }

    /**
     * This demo function is okay because the synchronized block is holding on same objects
     */
    private static void working_synchronization_loop() {
        Main main = new Main();
        counter = 0;
        int i;
        System.out.println("working synchronization");

        for (i = 0; i < 10000; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (main) {
                        counter--;
                    }
                }
            });

                Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (main) {
                        counter++;
                    }
                }
            });

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                System.err.println("Threads waiting interrupted " + e.getLocalizedMessage());
            }

            if (counter != 0) {
                break;
            }
        }

        if (counter == 0) {
            System.out.println("No faulty synchronization");
        }
        else {
            System.out.println("Faulty counter is " + counter + " at loop " + i);
        }
    }


    public static void main(String[] args) {
        faulty_synchronization_loop();
        working_synchronization_loop();
    }
}
