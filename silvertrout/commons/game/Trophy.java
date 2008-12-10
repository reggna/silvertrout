package silvertrout.commons.game;

public class Trophy {

    public enum Value {

        PLATINUM, GOLD, SILVER, BRONZE
    };
    private String name;
    private String archivment;
    private Value value;

    public Trophy(String name, String archivment, Value value) {
        this.name = name;
        this.archivment = archivment;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getArchivment() {
        return this.archivment;
    }

    public Value getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        Trophy t = (Trophy) o;
        return t.name.equals(this.name);
    }
}

