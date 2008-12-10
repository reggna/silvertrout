package silvertrout;

/**
 * Modes
 *
 *
 */
public class Modes {

    private String modes;

    public Modes() {
        this.modes = new String();
    }

    public Modes(String modes) {
        this.modes = modes;
    }

    public String get() {
        return this.modes;
    }

    public void set(String modes) {
        this.modes = modes;
    }

    public void giveMode(char m) {
        if (!haveMode(m)) {
            this.modes += m;
        }
    }

    public void takeMode(char m) {
        String newModes = new String();
        for (int i = 0; i < this.modes.length(); i++) {
            if (this.modes.charAt(i) != m) {
                newModes += this.modes.charAt(i);
            }
        }
        this.modes = newModes;
    }

    public boolean haveMode(char m) {
        return (this.modes.indexOf(m) >= 0);
    }
}
