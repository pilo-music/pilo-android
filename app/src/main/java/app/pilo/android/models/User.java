package app.pilo.android.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String access_token;
    private String name;
    private String email;
    private String phone;
    private String birth;
    private String gender;
    private String pic;

    @Ignore
    public User() {
        this.access_token = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.birth = "";
        this.gender = "";
        this.pic = "";
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
        this.access_token = access_token == null ? "" : access_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? "" : phone;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth == null ? "" : birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? "" : gender;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? "" : pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
