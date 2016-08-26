package stfalcon.universalpickerdialogsample.data;

/*
 * Created by troy379 on 25.08.16.
 */
public class Developer {

    private Level level;
    private Specialization specialization;
    private City location;

    public Developer(Level level, Specialization specialization, City location) {
        this.level = level;
        this.specialization = specialization;
        this.location = location;
    }

    public Level getLevel() {
        return level;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public City getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s developer in %s (%s)",
                level,
                specialization,
                location.getName(),
                location.getCountry());
    }

    public static class Level {

        private long id;
        private String name;

        public Level(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Specialization {

        private long id;
        private String name;

        public Specialization(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
