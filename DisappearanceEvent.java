
public class DisappearanceEvent {
    private Disappearable object;

    public DisappearanceEvent(Disappearable object) {
        this.object = object;
    }

    public Disappearable getObject() {
        return object;
    }
}
