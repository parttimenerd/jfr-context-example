package me.bechberger.server;

import jdk.jfr.ContextType;

import java.util.stream.Stream;

// Register the TracerContextType class as a context type
public class TraceContextTypeRegistration implements ContextType.Registration {

    @Override
    public Stream<Class<? extends ContextType>> types() {
        return Stream.of(TracerContextType.class);
    }
}
