package core;

public abstract class A_Component {

    public A_Component(ACCESS_TYPE accessType) {
        _ACCESS_TYPE = accessType;
    }

    public static class ComponentAlreadyInUseException extends RuntimeException {

        public ComponentAlreadyInUseException(String message) {
            super(message);
        }

    }

    public enum ACCESS_TYPE {
        SHARED,
        UNIQUE,
    }

    private boolean _inUse;

    private final ACCESS_TYPE _ACCESS_TYPE;

    protected final void p_use() {
        if (_inUse && _ACCESS_TYPE == ACCESS_TYPE.UNIQUE) throw new ComponentAlreadyInUseException(
                "[COMPONENT] : This component is already used and cannot be used by multiple entities!"
        );

        _inUse = true;
    }
    protected final void p_unuse() {
        _inUse = false;
    }

    /**
     * Returns the {@link ACCESS_TYPE} of this {@link A_Component}. <br>
     * The access type defines, whether a component can be used by multiple {@link Entity} objects,
     * or not.
     *
     * @return The access type of this component.
     *
     * @author Tim Kloepper
     */
    public ACCESS_TYPE getAccessType() {
        return _ACCESS_TYPE;
    }
    /**
     * Checks whether this component is used. <br>
     * If the {@link ACCESS_TYPE} is set to {@code SHARED}, this method
     * will always return {@code false}.
     *
     * @return Whether this component is used or not.
     *
     * @author Tim Kloepper
     */
    public boolean isInUse() {
        return _inUse;
    }

}