package cn.howardliu.effective.spring.springbootexception.domain;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2023-03-14
 */
public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private String grade;

    public Student() {
    }

    public Student(String id, String firstName, String lastName, String grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
