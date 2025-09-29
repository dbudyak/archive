package com.flibs.model;

/**
 * Created by dbudyak on 29.07.16.
 */
public class BookModel {

    public String Lang = "";
    public String BookId = "";
    public String Title = "";
    public String AvtorId = "";
    public String FirstName = "";
    public String MiddleName = "";
    public String LastName = "";
    public String Rating = "";

    @Override
    public String toString() {
        return "BookModel{" +
                "Lang='" + Lang + '\'' +
                ", BookId='" + BookId + '\'' +
                ", Title='" + Title + '\'' +
                ", AvtorId='" + AvtorId + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", MiddleName='" + MiddleName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Rating='" + Rating + '\'' +
                '}';
    }
}
