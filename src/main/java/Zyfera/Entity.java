package Zyfera;

/**
 * <p>
 *     Acts as a utility class for an entity inside {@link Zyfera} or rather a {@link Context}. <br>
 *     Provides all functionality for an entity.
 * </p>
 * <p>
 *     Also contains a {@link Stream} class for chained operations on the entity.
 * </p>
 *
 * @version 1.0
 *
 * @author Tim Kloepper
 */
public class Entity {

    protected Entity(int entityId, int contextId) {
        _contextId = contextId;

        _id = entityId;
    }

    /**
     * The utility class of the {@link Entity} class for chained operations.
     *
     * @version 1.0
     *
     * @author Tim Kloepper
     */
    public static class Stream {

        public Stream(Entity entity) {
            _ENTITY = entity;
        }

        private final Entity _ENTITY;

        /**
         * Attaches an {@link A_Component} to the {@link Entity} this {@link Stream} is of. <br>
         * All component classes are only represented once for every entity. <br>
         * The override operator determines if a component of the same class, which is possibly already attached to
         * the entity of this stream, should be overridden.
         *
         * @param component The component that should be added.
         * @param override Whether an already existing component should be overridden.
         *
         * @return This stream for chained operations.
         *
         * @param <T> The class of the component.
         *
         * @author Tim Kloepper
         */
        public <T extends A_Component> Stream attach(T component, boolean override) {
            Zyfera.zAddComponent(_ENTITY.contextId(), _ENTITY.id(), component, override);

            return this;
        }

        /**
         * Detaches an {@link A_Component} from the {@link Entity} this {@link Stream} is of.
         *
         * @param component The component that should be removed.
         *
         * @return This stream for chained operations.
         *
         * @param <T> The class of the component.
         *
         * @author Tim Kloepper
         */
        public <T extends A_Component> Stream detach(T component) {
            Zyfera.zRmvComponent(_ENTITY.contextId(), _ENTITY.id(), component);

            return this;
        }
        /**
         * Detaches an {@link A_Component}, based on its class, from the {@link Entity} this {@link Stream} is of.
         *
         * @param componentClass The class of the component that should be removed.
         *
         * @return This stream for chained operations.
         *
         * @param <T> The class of the component.
         *
         * @author Tim Kloepper
         */
        public <T extends A_Component> Stream detach(Class<T> componentClass) {
            Zyfera.zRmvComponent(_ENTITY.contextId(), _ENTITY.id(), componentClass);

            return this;
        }

        /**
         * Switches the {@link Context} of the {@link Entity} this stream is of to a new one.
         *
         * @param contextId The id of the new context.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public Stream switchContext(int contextId) {
            _ENTITY.switchContext(contextId);

            return this;
        }
        /**
         * Switches the {@link Context} of the {@link Entity} this stream is of to a new one.
         *
         * @param context The context.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public Stream switchContext(Context context) {
            _ENTITY.switchContext(context);

            return this;
        }

        /**
         * The id of the {@link Entity} this {@link Stream} is of.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public int id() {
            return _ENTITY.id();
        }
        /**
         * Returns the {@link Entity} this {@link Stream} is of.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public Entity entity() {
            return _ENTITY;
        }

    }

    private int _id, _contextId;

    private Stream _stream;

    /**
     * Attaches an {@link A_Component} to this {@link Entity}. <br>
     * Every component class can only be represented by one component inside an entity. <br>
     * The override operator determines, if a possibly already existing component of the same class,
     * gets replaced.
     *
     * @param component The component that should be added.
     * @param override Whether an already existing component should be overridden.
     *
     * @return The success of the attachment.
     *
     * @param <T> The class of the component.
     *
     * @author Tim Kloepper
     */
    public <T extends A_Component> boolean attach(T component, boolean override) {
        return Zyfera.zAddComponent(_contextId, _id, component, override);
    }

    /**
     * Detaches an {@link A_Component} from this {@link Entity}.
     *
     * @param component The component that should be removed.
     *
     * @return The success of the detachment.
     *
     * @param <T> The class of the component.
     *
     * @author Tim Kloepper
     */
    public <T extends A_Component> boolean detach(T component) {
        return Zyfera.zRmvComponent(_contextId, _id, component);
    }
    /**
     * Detaches an {@link A_Component} based on its class from this {@link Entity}.
     *
     * @param componentClass The class of the component that should be removed.
     *
     * @return The success of the detachment.
     *
     * @param <T> The class of the component.
     *
     * @author Tim Kloepper
     */
    public <T extends A_Component> boolean detach(Class<T> componentClass) {
        return Zyfera.zRmvComponent(_contextId, _id, componentClass);
    }

    /**
     * Switches this {@link Entity} to a new {@link Context} and returns its new id.
     *
     * @param toId The id of the new context.
     *
     * @return The new id of this entity.
     *
     * @author Tim Kloepper
     */
    public int switchContext(int toId) {
        return Zyfera.zSwitchContext(this, toId);
    }
    /**
     * Switches this {@link Entity} to a new {@link Context} and returns its new id.
     *
     * @param to The new context.
     *
     * @return The new id of this entity.
     *
     * @author Tim Kloepper
     */
    public int switchContext(Context to) {
        return Zyfera.zSwitchContext(this, to);
    }

    /**
     * Returns the id of this {@link Entity}.
     *
     * @return The id of this entity.
     */
    public int id() {
        return _id;
    }
    /**
     * The {@link Stream} utility class of this {@link Entity} for chained operations.
     *
     * @return The stream class of this entity.
     *
     * @author Tim Kloepper
     */
    public Stream stream() {
        if (_stream == null) _stream = new Stream(this);

        return _stream;
    }

    /**
     * Returns the id of the {@link Context} this {@link Entity} is in.
     *
     * @return The id of the context this entity is in.
     *
     * @author Tim Kloepper
     */
    public int contextId() {
        return _contextId;
    }
    /**
     * Returns the {@link Context} this {@link Entity} is in.
     *
     * @return The context this entity is in.
     */
    public Context context() {
        return Zyfera.zGetContext(_contextId);
    }

    protected void p_onContextSwitch(int newId, int newContextId) {
        _id = newId;
        _contextId = newContextId;
    }

}