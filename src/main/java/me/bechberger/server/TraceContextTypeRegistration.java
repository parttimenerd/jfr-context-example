package me.bechberger.server;

import com.google.auto.service.AutoService;
import jdk.jfr.ContextType;

import java.util.stream.Stream;

// Register the TracerContextType class as a context type
@AutoService(ContextType.Registration.class)
public class TraceContextTypeRegistration implements ContextType.Registration {

    @Override
    public Stream<Class<? extends ContextType>> types() {
        return Stream.of(TracerContextType.class);
    }
}
