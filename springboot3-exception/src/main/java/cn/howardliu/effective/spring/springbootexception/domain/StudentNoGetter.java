package cn.howardliu.effective.spring.springbootexception.domain;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2023-03-14
 */
public class StudentNoGetter {
    private String id;
    private String firstName;
    private String lastName;
    private String grade;

    public StudentNoGetter() {
    }

    public StudentNoGetter(String id, String firstName, String lastName, String grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
