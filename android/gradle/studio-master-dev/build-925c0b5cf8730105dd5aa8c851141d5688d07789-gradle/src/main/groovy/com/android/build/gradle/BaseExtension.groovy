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
package com.android.build.gradle
import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.AaptOptionsImpl
import com.android.build.gradle.internal.dsl.AndroidSourceSetFactory
import com.android.build.gradle.internal.dsl.DexOptionsImpl
import com.android.build.gradle.internal.dsl.ProductFlavorDsl
import com.android.build.gradle.internal.test.TestOptions
import com.android.builder.BuilderConstants
import com.android.builder.ProductFlavor
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
/**
 * Base android extension for all android plugins.
 */
public abstract class BaseExtension {

    private String target

    final ProductFlavor defaultConfig
    final AaptOptionsImpl aaptOptions
    final DexOptionsImpl dexOptions
    final TestOptions testOptions
    final CompileOptions compileOptions


    private final BasePlugin plugin
    private final DefaultDomainObjectSet<BuildVariant> buildVariants =
        new DefaultDomainObjectSet<BuildVariant>(BuildVariant.class)
    private final DefaultDomainObjectSet<BuildVariant> testBuildVariants =
        new DefaultDomainObjectSet<BuildVariant>(BuildVariant.class)

    /**
     * The source sets container.
     */
    final NamedDomainObjectContainer<AndroidSourceSet> sourceSetsContainer

    BaseExtension(BasePlugin plugin, ProjectInternal project, Instantiator instantiator) {
        this.plugin = plugin

        defaultConfig = instantiator.newInstance(ProductFlavorDsl.class, BuilderConstants.MAIN)

        aaptOptions = instantiator.newInstance(AaptOptionsImpl.class)
        dexOptions = instantiator.newInstance(DexOptionsImpl.class)
        testOptions = instantiator.newInstance(TestOptions.class)
        compileOptions = instantiator.newInstance(CompileOptions.class)

        sourceSetsContainer = project.container(AndroidSourceSet,
                new AndroidSourceSetFactory(instantiator, project.fileResolver))

        sourceSetsContainer.whenObjectAdded { AndroidSourceSet sourceSet ->
            ConfigurationContainer configurations = project.getConfigurations()

            Configuration compileConfiguration = configurations.findByName(
                    sourceSet.getCompileConfigurationName())
            if (compileConfiguration == null) {
                compileConfiguration = configurations.add(sourceSet.getCompileConfigurationName())
            }
            compileConfiguration.setVisible(false);
            compileConfiguration.setDescription(
                    String.format("Classpath for compiling the %s sources.", sourceSet.getName()))

            Configuration packageConfiguration = configurations.findByName(
                    sourceSet.getPackageConfigurationName())
            if (packageConfiguration == null) {
                packageConfiguration = configurations.add(sourceSet.getPackageConfigurationName())
            }
            packageConfiguration.setVisible(false)
            packageConfiguration.extendsFrom(compileConfiguration)
            packageConfiguration.setDescription(
                    String.format("Classpath packaged with the compiled %s classes.",
                            sourceSet.getName()));

            sourceSet.setRoot(String.format("src/%s", sourceSet.getName()))
        }
    }

    void compileSdkVersion(int apiLevel) {
        this.target = "android-" + apiLevel
    }

    void setCompileSdkVersion(int apiLevel) {
        compileSdkVersion(apiLevel)
    }

    void compileSdkVersion(String target) {
        this.target = target
    }

    void setCompileSdkVersion(String target) {
        compileSdkVersion(target)
    }

    void sourceSets(Action<NamedDomainObjectContainer<AndroidSourceSet>> action) {
        action.execute(sourceSetsContainer)
    }

    void defaultConfig(Action<ProductFlavor> action) {
        action.execute(defaultConfig)
    }

    void aaptOptions(Action<AaptOptionsImpl> action) {
        action.execute(aaptOptions)
    }

    void dexOptions(Action<DexOptionsImpl> action) {
        action.execute(dexOptions)
    }

    void testOptions(Action<TestOptions> action) {
        action.execute(testOptions)
    }

    void compileOptions(Action<CompileOptions> action) {
        action.execute(testOptions)
    }

    public DefaultDomainObjectSet<BuildVariant> getBuildVariants() {
        plugin.createAndroidTasks()
        return buildVariants
    }

    public DefaultDomainObjectSet<BuildVariant> getTestBuildVariants() {
        plugin.createAndroidTasks()
        return testBuildVariants
    }

    public String getCompileSdkVersion() {
        return target
    }
}
