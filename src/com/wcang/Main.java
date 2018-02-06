package com.wcang;


import java.util.concurrent.CountDownLatch;

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
            CountDownLatch doneSignal = new CountDownLatch(2);
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (main) {
                        counter--;
                    }
                    doneSignal.countDown();
                }
            });

                Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (main) {
                        counter++;
                    }
                    doneSignal.countDown();
                }
            });

            t1.start();
            t2.start();

            try {
                doneSignal.await();
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
     * bad example where exceptions will be thrown when wait and notify are called. This
     * is because the intrinsic lock is hold on main object instead of runnable
     * objects, while wait and notify are called on runnable objects.
     */
    private static void exception_wait_notify() {
        Main main = new Main();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (main) {
                    System.out.println("Before wait");
                    try {
                        wait();
                    } catch (InterruptedException exc) {
                        System.err.println("Exception on wait: " + exc.getLocalizedMessage());
                    }
                    System.out.println("After wait");
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (main) {
                    System.out.println("Before notify");
                    try {
                        Thread.sleep(1000);
                        notify();
                    } catch (InterruptedException exc) {
                        System.err.println("Exception on sleep: " + exc.getLocalizedMessage());
                    }
                    System.out.println("After notify");
                }
            }
        });
        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exc) {
            System.err.println("indefinite_wait interrupted: " + exc.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {
        faulty_synchronization_loop();
        working_synchronization_loop();
        exception_wait_notify();
    }
}
