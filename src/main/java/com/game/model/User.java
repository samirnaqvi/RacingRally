package com.game.model;

public class User implements Comparable {
    private int id;
    private String name;
    private int highestScore;

    public User(String name) {
        this.name = name;
    }
/*Attributes of the User include the ID, the name, and the user's highest score, which is also recorded for the top 10 scorer in the database and the high scores section.*/
    public User(int id, String name, int highestScore) {
        this.id = id;
        this.name = name;
        this.highestScore = highestScore;
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name, int highestScore) {
        this.name = name;
        this.highestScore = highestScore;
    }

    public User() {
    }

    public int getId() {
        return id;//gets id of the User
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;//gets the Name of the user, custom created by the User him/herself
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {

        this.highestScore = highestScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (highestScore != user.highestScore) return false;
        return !(name != null ? !name.equals(user.name) : user.name != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + highestScore;
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object o) { //sorts the highest scoring users
        if (this.highestScore > ((User) o).getHighestScore()) return -1;
        else if (this.highestScore == ((User) o).getHighestScore()) return 0;
        else return 1;
    }
}
