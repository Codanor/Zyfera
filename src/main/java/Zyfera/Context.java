package Zyfera;

public class Context {

    protected Context(int id) {
        _ID = id;
    }

    public class Stream {

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

        public Stream attach(A_Processor processor) {
            if (!Zyfera.zAttachProcessor(_ID, processor)) throw new FailedToAttachProcessorException(
                    "[CONTEXT STREAM] : Failed to attach the processor to the context : " + id()
            );

            return this;
        }
        public Stream detach(A_Processor processor) {
            if (!Zyfera.zDetachProcessor(_ID, processor)) throw new FailedToDetachProcessorException(
                    "[CONTEXT STREAM] : Failed to detach the processor to the context : " + id()
            );

            return this;
        }

        public int id() {
            return _CONTEXT.id();
        }
        public Context context() {
            return _CONTEXT;
        }

    }

    private Stream _stream;

    private final int _ID;

    public boolean attach(A_Processor processor) {
        return Zyfera.zAttachProcessor(_ID, processor);
    }
    public boolean detach(A_Processor processor) {
         return Zyfera.zDetachProcessor(_ID, processor);
    }

    public int createEntityId() {
        return Zyfera.zCreateEntityId(this);
    }

    public Entity createEntity() {
        return Zyfera.zCreateEntity(this);
    }

    public int id() {
        return _ID;
    }
    public Stream stream() {
        if (_stream == null) _stream = new Stream(this);

        return _stream;
    }

}