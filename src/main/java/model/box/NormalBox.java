package model.box;

public class NormalBox extends Box {

    @Override
    public BoxType getType() {
        return BoxType.NORMAL;
    }

    @Override
    public PushResult push() {
        throw new UnsupportedOperationException("TODO");
    }
}
