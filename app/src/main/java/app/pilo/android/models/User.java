package app.pilo.android.models;

public class User {
    private String access_token;
    private String name;
    private String email;
    private String phone;
    private String birth;
    private String gender;
    private String pic;

    public User() {
        this.access_token = null;
        this.name = null;
        this.email = null;
        this.phone = null;
        this.birth = null;
        this.gender = null;
        this.pic = null;
    }

    public User(String access_token, String name, String email, String phone, String birth, String gender, String pic) {
        this.access_token = access_token;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
        this.pic = pic;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
