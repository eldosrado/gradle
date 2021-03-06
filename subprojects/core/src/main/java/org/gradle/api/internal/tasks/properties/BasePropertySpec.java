/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.properties;

import groovy.lang.Closure;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.changedetection.state.FileCollectionSnapshotter;
import org.gradle.api.internal.changedetection.state.GenericFileCollectionSnapshotter;
import org.gradle.api.internal.changedetection.state.SnapshotNormalizationStrategy;
import org.gradle.api.internal.changedetection.state.TaskFilePropertyCompareStrategy;
import org.gradle.api.internal.changedetection.state.TaskFilePropertySnapshotNormalizationStrategy;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskOutputFilePropertyBuilder;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.util.DeprecationLogger;

import static org.gradle.api.internal.changedetection.state.TaskFilePropertyCompareStrategy.OUTPUT;

abstract class BasePropertySpec extends AbstractTaskPropertyBuilder implements TaskOutputPropertySpecAndBuilder, TaskOutputFilePropertyBuilder {
    private final TaskOutputs taskOutputs;
    private boolean optional;
    private SnapshotNormalizationStrategy snapshotNormalizationStrategy = TaskFilePropertySnapshotNormalizationStrategy.ABSOLUTE;

    protected BasePropertySpec(TaskOutputs taskOutputs) {
        this.taskOutputs = taskOutputs;
    }

    @Override
    public TaskOutputFilePropertyBuilder withPropertyName(String propertyName) {
        setPropertyName(propertyName);
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public TaskOutputFilePropertyBuilder optional() {
        return optional(true);
    }

    @Override
    public TaskOutputFilePropertyBuilder optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public SnapshotNormalizationStrategy getSnapshotNormalizationStrategy() {
        return snapshotNormalizationStrategy;
    }

    @Override
    public TaskOutputFilePropertyBuilder withPathSensitivity(PathSensitivity sensitivity) {
        this.snapshotNormalizationStrategy = TaskFilePropertySnapshotNormalizationStrategy.valueOf(sensitivity);
        return this;
    }

    public TaskFilePropertyCompareStrategy getCompareStrategy() {
        return OUTPUT;
    }

    @Override
    public String toString() {
        return getPropertyName() + " (" + getCompareStrategy() + " " + snapshotNormalizationStrategy + ")";
    }

    // --- Deprecated delegate methods

    private TaskOutputs getTaskOutputs(String method) {
        DeprecationLogger.nagUserOfDiscontinuedMethod("chaining of the " + method, String.format("Use '%s' on TaskOutputs directly instead.", method));
        return taskOutputs;
    }

    public Class<? extends FileCollectionSnapshotter> getSnapshotter() {
        return GenericFileCollectionSnapshotter.class;
    }

    @Override
    public void upToDateWhen(Closure upToDateClosure) {
        getTaskOutputs("upToDateWhen(Closure)").upToDateWhen(upToDateClosure);
    }

    @Override
    public void upToDateWhen(Spec<? super Task> upToDateSpec) {
        getTaskOutputs("upToDateWhen(Spec)").upToDateWhen(upToDateSpec);
    }

    @Override
    public void cacheIf(Spec<? super Task> spec) {
        getTaskOutputs("cacheIf(Spec)").cacheIf(spec);
    }

    @Override
    public boolean getHasOutput() {
        return getTaskOutputs("getHasOutput()").getHasOutput();
    }

    @Override
    public FileCollection getFiles() {
        return getTaskOutputs("getFiles()").getFiles();
    }

    @Override
    @Deprecated
    public TaskOutputFilePropertyBuilder files(Object... paths) {
        return getTaskOutputs("files(Object...)").files(paths);
    }

    @Override
    public TaskOutputFilePropertyBuilder file(Object path) {
        return getTaskOutputs("file(Object)").file(path);
    }

    @Override
    public TaskOutputFilePropertyBuilder dir(Object path) {
        return getTaskOutputs("dir(Object)").dir(path);
    }

    @Override
    public int compareTo(TaskPropertySpec o) {
        return getPropertyName().compareTo(o.getPropertyName());
    }
}
