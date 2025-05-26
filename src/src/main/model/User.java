package src.main.model;


public class User {
    private String username;  //
    private  String pw;
    private String nickname;

    public User(String username, String pw, String nickname) {
        this.username = username;
        this.pw = pw;
        this.nickname = nickname;
    }

    public String getId() { return username;}

    public void setId(String username) { this.username = username;}

    public String getPassword() { return pw;}

    public void setPassword(String pw) { this.pw = pw;}

    public String getNickname() { return nickname;}

    public void setNickname(String nickname) { this.nickname = nickname;}

}
