package com.zyccx.tutorial.example;

public class ThreadLocalTestDemo4 {
    public static void main(String args[]) {
        new Thread(() -> {
            Message msgA = new Message();
            msgA.setNote("中国矿业大学北京");
            MyUtil.set(msgA);
            new MessageConsumer().print();
        }, "学生A").start();
        new Thread(() -> {
            Message msgB = new Message();
            msgB.setNote("清华大学");
            MyUtil.set(msgB);
            new MessageConsumer().print();
        }, "学生B").start();
    }


    static class Message {
        private String note;

        public void setNote(String note) {
            this.note = note;
        }

        public String getNote() {
            return this.note;
        }
    }

    static class MessageConsumer {
        public void print() {
            System.out.println(Thread.currentThread().getName() + MyUtil.get().getNote());
        }
    }

    static class MyUtil {
        private static ThreadLocal<Message> threadLocal = new ThreadLocal<>();

        public static void set(Message msg) {
            threadLocal.set(msg);
        }

        public static Message get() {
            return threadLocal.get();
        }
    }
}

