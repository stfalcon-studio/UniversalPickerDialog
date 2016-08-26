package stfalcon.universalpickerdialogsample.data;

/*
 * Created by troy379 on 23.08.16.
 */
public class City {

    private String name;
    private String country;

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return name;
    }

}
