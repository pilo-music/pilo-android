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
    private boolean global_notification;
    private boolean music_notification;
    private boolean album_notification;
    private boolean video_notification;

    public User() {
        this.access_token = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.birth = "";
        this.gender = "";
        this.pic = "";
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

    public boolean isGlobal_notification() {
        return global_notification;
    }

    public void setGlobal_notification(boolean global_notification) {
        this.global_notification = global_notification;
    }

    public boolean isMusic_notification() {
        return music_notification;
    }

    public void setMusic_notification(boolean music_notification) {
        this.music_notification = music_notification;
    }

    public boolean isAlbum_notification() {
        return album_notification;
    }

    public void setAlbum_notification(boolean album_notification) {
        this.album_notification = album_notification;
    }

    public boolean isVideo_notification() {
        return video_notification;
    }

    public void setVideo_notification(boolean video_notification) {
        this.video_notification = video_notification;
    }
}
