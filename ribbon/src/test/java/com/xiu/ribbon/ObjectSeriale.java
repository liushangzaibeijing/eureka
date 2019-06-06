package com.xiu.ribbon;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectSeriale {

    @Test
    public void testOBj(){
        Student student = new Student();
        student.setAge(23);
        student.setName("xieqx");


        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("D:\\aa.txt"));

            objectOutputStream.writeObject(student);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testOBjJson(){
        Student student = new Student();
        student.setAge(23);
        student.setName("xieqx");

        String json = JsonUtil.obj2str(student);
        System.out.println(json);
    }
}
