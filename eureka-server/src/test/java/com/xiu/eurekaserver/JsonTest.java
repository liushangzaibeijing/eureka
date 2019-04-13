package com.xiu.eurekaserver;

import com.xiu.eurekaserver.utils.JsonUtil;
import org.junit.Test;

import java.util.Date;

public class JsonTest {

    @Test
    public void testJson(){

        Student student = new Student();
        student.setAge(23);
        student.setBirthday(new Date());
        student.setName("鞋企秀");

        System.out.println(JsonUtil.obj2str(student));
    }


    class Student{
        private String name;

        private Integer age;

        private Date birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }
}
