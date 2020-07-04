package com.it.eventbuscustomsimple;

/**
 * Created by lgc on 2020-02-23.
 */
public class EventBean {
    String name;
    int age;

    public EventBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
