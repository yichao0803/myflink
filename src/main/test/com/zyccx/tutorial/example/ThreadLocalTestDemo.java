package com.zyccx.tutorial.example;

public class ThreadLocalTestDemo {

    private static ThreadLocal<String> sThreadLocal=new ThreadLocal<>();

    public static void main(String args[]) {
        //主线程
        sThreadLocal.set("这是在主线程中");
        System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
        //线程a
        new Thread(new Runnable() {
            public void run() {
                sThreadLocal.set("这是在线程a中");
                System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
            }
        }, "线程a").start();
        //线程b
        new Thread(new Runnable() {
            public void run() {
                sThreadLocal.set("这是在线程b中");
                System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
            }
        }, "线程b").start();
        //线程c  这个线程是使Lambda建立的，跟上面两个是一样的。不会使用Lambda表达式的同学请看我的另一篇博客
        new Thread(() -> {
            sThreadLocal.set("这是在线程c中");
            System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
        }, "线程c").start();
    }
}
