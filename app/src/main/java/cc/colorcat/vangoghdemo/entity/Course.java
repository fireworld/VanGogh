package cc.colorcat.vangoghdemo.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Course info.
 * Created by cxx on 15/12/1.
 * xx.ch@outlook.com
 */
public class Course {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("picSmall")
    private String picSmallUrl;
    @SerializedName("picBig")
    private String picBigUrl;
    @SerializedName("description")
    private String description;
    @SerializedName("learner")
    private int numOfLearner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicSmallUrl() {
        return picSmallUrl;
    }

    public void setPicSmallUrl(String picSmallUrl) {
        this.picSmallUrl = picSmallUrl;
    }

    public String getPicBigUrl() {
        return picBigUrl;
    }

    public void setPicBigUrl(String picBigUrl) {
        this.picBigUrl = picBigUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumOfLearner() {
        return numOfLearner;
    }

    public void setNumOfLearner(int numOfLearner) {
        this.numOfLearner = numOfLearner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (id != course.id) return false;
        if (numOfLearner != course.numOfLearner) return false;
        if (name != null ? !name.equals(course.name) : course.name != null) return false;
        if (picSmallUrl != null ? !picSmallUrl.equals(course.picSmallUrl) : course.picSmallUrl != null)
            return false;
        if (picBigUrl != null ? !picBigUrl.equals(course.picBigUrl) : course.picBigUrl != null)
            return false;
        return description != null ? description.equals(course.description) : course.description == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (picSmallUrl != null ? picSmallUrl.hashCode() : 0);
        result = 31 * result + (picBigUrl != null ? picBigUrl.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + numOfLearner;
        return result;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picSmallUrl='" + picSmallUrl + '\'' +
                ", picBigUrl='" + picBigUrl + '\'' +
                ", description='" + description + '\'' +
                ", numOfLearner=" + numOfLearner +
                '}';
    }
}
