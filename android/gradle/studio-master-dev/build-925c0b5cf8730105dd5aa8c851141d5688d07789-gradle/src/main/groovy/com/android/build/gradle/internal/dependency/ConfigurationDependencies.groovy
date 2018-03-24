/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.build.gradle.internal.dependency

import com.android.annotations.NonNull
import com.android.build.gradle.AndroidSourceSet
import com.android.builder.JarDependency
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * Object that represents the dependencies of a configuration, and optionally contains the
 * dependencies for a test config for the given config.
 */
public class ConfigurationDependencies {

    public static enum ConfigType { DEFAULT, FLAVOR, BUILDTYPE }

    final Project project
    final AndroidSourceSet sourceSet
    final ConfigType type
    ConfigurationDependencies testConfigDependencies;

    DependencyChecker checker

    protected ConfigurationDependencies(@NonNull Project project,
                                        @NonNull AndroidSourceSet sourceSet,
                                        @NonNull ConfigType type) {
        this.project = project
        this.sourceSet = sourceSet
        this.type = type
    }

    List<AndroidDependencyImpl> libraries
    List<JarDependency> jars

    public Configuration getConfiguration() {
        return project.configurations[sourceSet.compileConfigurationName]
    }

    public String getConfigBaseName() {
        return sourceSet.name
    }
}
