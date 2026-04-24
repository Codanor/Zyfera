package Zyfera;

public abstract class A_Component {

    public A_Component(ACCESS_TYPE accessType) {
        _ACCESS_TYPE = accessType;
    }

    public enum ACCESS_TYPE {
        SHARED,
        UNIQUE,
    }

    private boolean _inUse;

    private final ACCESS_TYPE _ACCESS_TYPE;

    protected void use() {
        if (_inUse && _ACCESS_TYPE == ACCESS_TYPE.UNIQUE) throw new RuntimeException();

        _inUse = true;
    }
    protected void unuse() {
        _inUse = false;
    }

}