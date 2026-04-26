package core;

/**
 * <p>
 *     Acts as a utility class for a context inside {@link Zyfera}. <br>
 *     Provides all functionality for a context.
 * </p>
 * <p>
 *     Also contains a {@link Stream} class for chained operations on the context.
 * </p>
 *
 * @version 1.0
 *
 * @author Tim Kloepper
 */
public class Context {

    protected Context(int id) {
        _disposed = false;

        _ID = id;
    }

    public static class ContextIsDisposedException extends RuntimeException {

        public ContextIsDisposedException(String message) {
            super(message);
        }

    }

    /**
     * The utility class of the {@link Context} class for chained operations.
     *
     * @version 1.0
     *
     * @author Tim Kloepper
     */
    public static class Stream {

        private Stream(Context context) {
            _CONTEXT = context;
        }

        public static class FailedToAttachProcessorException extends RuntimeException {

            public FailedToAttachProcessorException(String message) {
                super(message);
            }

        }
        public static class FailedToDetachProcessorException extends RuntimeException {

            public FailedToDetachProcessorException(String message) {
                super(message);
            }

        }

        private final Context _CONTEXT;

        /**
         * Attaches an {@link A_Processor} to the {@link Context} this {@link Stream} is of.
         *
         * @param processor The processor that should be added to the context.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public Stream attach(A_Processor processor) {
            if (!Zyfera.zAttachProcessor(_CONTEXT.id(), processor)) throw new FailedToAttachProcessorException(
                    "[CONTEXT STREAM] : Failed to attach the processor to the context : " + id()
            );

            return this;
        }
        /**
         * Detaches an {@link A_Processor} from the {@link Context} this {@link Stream} is of.
         *
         * @param processor The processor that should be removed from the context.
         *
         * @return This stream for chained operations.
         *
         * @author Tim Kloepper
         */
        public Stream detach(A_Processor processor) {
            if (!Zyfera.zDetachProcessor(_CONTEXT.id(), processor)) throw new FailedToDetachProcessorException(
                    "[CONTEXT STREAM] : Failed to detach the processor to the context : " + id()
            );

            return this;
        }

        /**
         * Returns the id of the {@link Context} this {@link Stream} is of.
         *
         * @return The id of the context this stream is of.
         *
         * @author Tim Kloepper
         */
        public int id() {
            return _CONTEXT.id();
        }
        /**
         * Returns the {@link Context} this {@link Stream} is of.
         *
         * @return The context this stream is of.
         *
         * @author Tim Kloepper
         */
        public Context context() {
            return _CONTEXT;
        }

    }

    private boolean _disposed;

    private Stream _stream;

    private final int _ID;

    /**
     * Attaches an {@link A_Processor} to this {@link Context}. <br>
     * Every processor can only ever be in a context once at a time.
     *
     * @param processor The processor that should be attached.
     *
     * @return The success of the attachment.
     *
     * @author Tim Kloepper
     */
    public boolean attach(A_Processor processor) {
        _checkDisposed();

        return Zyfera.zAttachProcessor(_ID, processor);
    }
    /**
     * Detaches an {@link A_Processor} from this {@link Context}. <br>
     *
     * @param processor The processor that should be removed.
     *
     * @return The success of the detachment.
     *
     * @author Tim Kloepper
     */
    public boolean detach(A_Processor processor) {
        _checkDisposed();

         return Zyfera.zDetachProcessor(_ID, processor);
    }

    /**
     * Creates an {@link Entity} inside this {@link Context} and returns its id.
     *
     * @return The id of the newly created entity.
     *
     * @author Tim Kloepper
     */
    public int createEntityId() {
        _checkDisposed();

        return Zyfera.zCreateEntityId(this);
    }

    /**
     * Creates an {@link Entity} inside this {@link Context} and returns it.
     *
     * @return The newly created entity.
     *
     * @author Tim Kloepper
     */
    public Entity createEntity() {
        _checkDisposed();

        return Zyfera.zCreateEntity(this);
    }

    /**
     * Returns the utility {@link Entity} class of an entity, specified by its id.
     *
     * @param entityId The id of the entity.
     *
     * @return The entity utility class of the entity.
     *
     * @author Tim Kloepper
     */
    public Entity getEntity(int entityId) {
        _checkDisposed();

        return Zyfera.zGetEntity(entityId, _ID);
    }

    /**
     * Returns the id of this {@link Context}.
     *
     * @return The id of this context.
     *
     * @author Tim Kloepper
     */
    public int id() {
        _checkDisposed();

        return _ID;
    }
    /**
     * Returns the utility {@link Stream} class of this {@link Context}.
     *
     * @return The stream of this context.
     *
     * @author Tim Kloepper
     */
    public Stream stream() {
        _checkDisposed();

        if (_stream == null) _stream = new Stream(this);

        return _stream;
    }

    /**
     * Disposes this context, making it invalid for further interactions and removing it form {@link Zyfera}.
     *
     * @author Tim Kloepper
     */
    public void dispose() {
        _checkDisposed();

        Zyfera.zDisposeContext(this);

        _disposed = true;
    }
    /**
     * Checks, whether this {@link Context} is disposed, or not.
     *
     * @return Whether this context is disposed or not.
     */
    public boolean isDisposed() {
        return _disposed;
    }

    private void _checkDisposed() {
        if (_disposed) throw new ContextIsDisposedException("[CONTEXT] : This context is disposed and cannot be interacted with!");
    }

    /**
     * Disposes an {@link Entity}, making it invalid for further interactions and removing it from this {@link Context} and {@link Zyfera}.
     *
     * @param entityId The id of the entity.
     *
     * @author Tim Kloepper
     */
    public void disposeEntity(int entityId) {
        Zyfera.zDisposeEntity(_ID, entityId);
    }
    /**
     * Disposes an {@link Entity}, making it invalid for further interactions and removing it from this {@link Context} and {@link Zyfera}.
     *
     * @param entity The entity.
     *
     * @author Tim Kloepper
     */
    public void disposeEntity(Entity entity) {
        Zyfera.zDisposeEntity(entity);
    }

    /**
     * Updates this {@link Context} by updating all {@link A_Processor} objects with the {@link Entity} object in this context.
     *
     * @author Tim Kloepper
     */
    public void update() {
        Zyfera.zUpdateContext(this);
    }

}