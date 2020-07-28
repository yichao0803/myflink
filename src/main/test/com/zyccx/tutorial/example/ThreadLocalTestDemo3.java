package com.zyccx.tutorial.example;

public class ThreadLocalTestDemo3 {

    static class Message{
        private String note;
        public void setNote(String note) {
            this.note=note;
        }
        public String getNote() {
            return this.note;
        }
    }
    static   class MessageConsumer{
        public void print() {
            System.out.println(Thread.currentThread().getName()+MyUtil.msg.getNote());
        }
    }

    static class MyUtil{
        public static Message msg;
    }

    public static void main(String args[]) {
        new Thread(()->{
            Message msgA=new Message();
            msgA.setNote("中国矿业大学北京");
            MyUtil.msg=msgA;
            new MessageConsumer().print();
        },"学生A") .start();
        new Thread(()->{
            Message msgB=new Message();
            msgB.setNote("清华大学");
            MyUtil.msg=msgB;
            new MessageConsumer().print();
        },"学生B").start();
    }
}
