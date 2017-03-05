package cloud.himanshu.internshipcamp17;

/**
 * Created by Himanshu on 28-Feb-17.
 */

public class Student {
    static String Name, Contact, Univ_name, Univ_roll, Branch, Year, email, Verified, resume_link, Code, password, Payment, Source;
    static int resume_uploaded;

    Student(){

    }

    public static Student newInstance(){
        Student stu = new Student();
        return stu;
    }

    public static void setBranch(String branch) {
        Branch = branch;
    }

    public static void setCode(String code) {
        Code = code;
    }

    public static void setContact(String contact) {
        Contact = contact;
    }

    public static void setEmail(String email) {
        Student.email = email;
    }

    public static void setName(String name) {
        Name = name;
    }

    public static void setPassword(String password) {
        Student.password = password;
    }

    public static void setPayment(String payment) {
        Payment = payment;
    }

    public static void setResume_link(String resume_link) {
        Student.resume_link = resume_link;
    }

    public static void setResume_uploaded(int resume_uploaded) {
        Student.resume_uploaded = resume_uploaded;
    }

    public static void setSource(String source) {
        Source = source;
    }

    public static void setUniv_name(String univ_name) {
        Univ_name = univ_name;
    }

    public static void setUniv_roll(String univ_roll) {
        Univ_roll = univ_roll;
    }

    public static void setVerified(String verified) {
        Verified = verified;
    }

    public static void setYear(String year) {
        Year = year;
    }

    public static String getBranch() {
        return Branch;
    }

    public static String getCode() {
        return Code;
    }

    public static int getResume_uploaded() {
        return resume_uploaded;
    }

    public static String getContact() {
        return Contact;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return Name;
    }

    public static String getPassword() {
        return password;
    }

    public static String getPayment() {
        return Payment;
    }

    public static String getResume_link() {
        return resume_link;
    }

    public static String getSource() {
        return Source;
    }

    public static String getUniv_name() {
        return Univ_name;
    }

    public static String getUniv_roll() {
        return Univ_roll;
    }

    public static String getVerified() {
        return Verified;
    }

    public static String getYear() {
        return Year;
    }
}
