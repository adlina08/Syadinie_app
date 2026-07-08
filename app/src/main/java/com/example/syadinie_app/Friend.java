package com.example.syadinie_app;

public class Friend {
    private int id;
    private String name, gender, hp, email, addr1, addr2, addr3, addr4;

    public Friend(int id, String name, String gender, String hp, String email,
                  String addr1, String addr2, String addr3, String addr4) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.hp = hp;
        this.email = email;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.addr3 = addr3;
        this.addr4 = addr4;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getHp() { return hp; }
    public String getEmail() { return email; }
    public String getAddr1() { return addr1; }
    public String getAddr2() { return addr2; }
    public String getAddr3() { return addr3; }
    public String getAddr4() { return addr4; }

    // IMPORTANT: ArrayAdapter's built-in filter matches against toString()
    @Override
    public String toString() {
        return name;
    }
}