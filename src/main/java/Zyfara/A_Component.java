package Zyfara;

public abstract class A_Component {

    public A_Component(ACCESS accessType) {
        _used = false;

        _ACCESS_TYPE = accessType;
    }

    public enum ACCESS {
        SHARED,
        UNIQUE
    }

    private boolean _used;

    private final ACCESS _ACCESS_TYPE;

    public ACCESS getAccessType() {
        return _ACCESS_TYPE;
    }

    public void use() {
        if (_ACCESS_TYPE == ACCESS.UNIQUE && _used) throw new ComponentAlreadyInUse("[COMPONENT] : This component is already used and is not set to SHARED!");

        _used = true;
    }
    public void unuse() {
        _used = false;
    }

    public abstract A_Component copy();

}