package com.zyccx.tutorial.example;

/**
 * https://blog.csdn.net/yaoyaoyao_123/article/details/84727559
 */
public class ThreadLocalTestDemo2 {

    public static void main(String args[]) {
        new Thread(() -> {
            Message msgA = new Message();
            msgA.setNote("中国矿业大学北京");
            new MessageConsumer().print(msgA);
        }, "学生A").start();
        new Thread(() -> {
            Message msgB = new Message();
            msgB.setNote("清华大学");
            new MessageConsumer().print(msgB);
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
        public void print(Message msg) {
            System.out.println(Thread.currentThread().getName() + msg.getNote());
        }
    }

}
