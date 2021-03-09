package com.cs65.homie.models;

public enum GenderEnum
{

    FEMALE, MALE, NONE;

    public static GenderEnum fromInt(int g)
    {
        switch (g)
        {
            case 0:
                return GenderEnum.MALE;
            case 1:
                return GenderEnum.FEMALE;
            default:
                return GenderEnum.NONE;
        }
    }

}
