package me.ikevoodoo.smpcore.config2;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;

import java.io.IOException;
import java.lang.annotation.Annotation;

public interface ConfigAnnotationProcessor<T> {

    T process(Annotation annotation, YamlDocument document, Route route, Object[] params, Object defaultParam) throws IOException;

}
