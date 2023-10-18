package me.bechberger.server;

import com.google.auto.service.AutoService;
import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.ContextType;

import java.util.concurrent.atomic.AtomicLong;

@Name("tracer-context")
@Description("Tracer context type tuple")
@AutoService(ContextType.class)
public class TracerContextType extends ContextType implements AutoCloseable {

    private static final AtomicLong traceIdCounter = new AtomicLong(0);

    // attributes are defined as plain public fields annotated by at least @Name annotation
    @Name("user")
    @Description("Registered user")
    public String user;

    @Name("action")
    @Description("Action: register, store, load, delete")
    public String action;

    @Name("file")
    @Description("File if passed")
    public String file;

    // currently no primitives allowed here
    @Name("trace")
    public String traceId;

    public TracerContextType(String user, String action, String file) {
        this.user = user;
        this.action = action;
        this.file = file;
        this.traceId = "" + traceIdCounter.incrementAndGet();
        this.set();
    }

    public TracerContextType(String user, String action) {
        this(user, action,"");
    }

    @Override
    public void close() throws Exception {
        unset();
    }
}