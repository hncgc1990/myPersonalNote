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

import com.android.builder.AndroidDependency
import com.android.builder.BundleDependency
import com.android.builder.ManifestDependency

class AndroidDependencyImpl extends BundleDependency {
    final List<AndroidDependency> dependencies;
    final File bundle

    AndroidDependencyImpl(String name, File explodedBundle, List<AndroidDependency> dependencies,
                          File bundle) {
        super(explodedBundle, name)
        this.dependencies = dependencies
        this.bundle = bundle
    }

    @Override
    List<ManifestDependency> getManifestDependencies() {
        return dependencies
    }
}
