/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.jetbrains.kotlin.konan.file.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions

@Extensions(
    ExtendWith(ForgeExtension::class)
)
internal class InternalApiUsageDetectorTest {

    @StringForgery(regex = "(com|org|fr)\\.[a-z]{1,10}")
    lateinit var nonOpenObservePackage: String

    @StringForgery(regex = "com\\.datadog(|\\.[a-z]{1,13})")
    lateinit var ooPackage: String

    @Test
    fun `M report issue W internal class is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalClassKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.InternalSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       InternalSdkClass instance = new InternalSdkClass();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:8: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           InternalSdkClass instance = new InternalSdkClass();
                                                       ~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal method is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.SomeSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SomeSdkClass instance = new SomeSdkClass();
                       instance.internalMethod();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:9: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           instance.internalMethod();
                           ~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal property is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalPropertyKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.SomeSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SomeSdkClass instance = new SomeSdkClass();
                       instance.getInternalProperty();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:9: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           instance.getInternalProperty();
                           ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W method of internal interface is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInInterfaceKotlinStub,
                classImplementingInternalInterfaceKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.SdkCore;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SdkCore instance = new SdkCore();
                       instance.internalMethod();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:9: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           instance.internalMethod();
                           ~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal extension method is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalExtensionMethodKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.TestKt;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       TestKt.internalMethod("something");
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:8: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           TestKt.internalMethod("something");
                           ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal method in object class is used from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInObjectClassKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.OpenObserve;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       OpenObserve._internalProxy();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                    src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.java:8: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                           OpenObserve._internalProxy();
                           ~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal class is used from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalClassKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.InternalSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = InternalSdkClass()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                   src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.kt:8: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                          val instance = InternalSdkClass()
                                         ~~~~~~~~~~~~~~~~~~
                   1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal method is used from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.SomeSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SomeSdkClass()
                       instance.internalMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                   src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.kt:9: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                          instance.internalMethod()
                          ~~~~~~~~~~~~~~~~~~~~~~~~~
                   1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal property is used from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalPropertyKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.SomeSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SomeSdkClass()
                       instance.internalProperty
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                   src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.kt:9: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                          instance.internalProperty
                                   ~~~~~~~~~~~~~~~~
                   1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal extension method is used from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalExtensionMethodKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.internalMethod
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       // lint will tun this twice in 2 different modes
                       // and reporting outputs will vary: with quotes vs without for reporting
                       // location if call is directly like "something".internalMethod()
                       // so creating temporary variable
                       val literal = "something"
                       literal.internalMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                   src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.kt:13: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                          literal.internalMethod()
                          ~~~~~~~~~~~~~~~~~~~~~~~~
                   1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M report issue W internal method in object class is used from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInObjectClassKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.OpenObserve
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       OpenObserve._internalProxy()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect(
                """
                   src/${nonOpenObservePackage.packageToPath()}/ClassUnderTest.kt:8: Error: Symbols annotated with com.openobserve.android.lint.InternalApi shouldn't be used outside of OpenObserve SDK packages. [OpenObserveInternalApiUsage]
                          OpenObserve._internalProxy()
                          ~~~~~~~~~~~~~~~~~~~~~~~~
                   1 errors, 0 warnings
                """
            )
    }

    @Test
    fun `M not report issue W internal method is used from non-OpenObserve package { kotlin + package-less file }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodKotlinStub,
                kotlin(
                    """                
                import com.openobserve.android.SomeSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SomeSdkClass()
                       instance.internalMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal class is used from OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalClassKotlinStub,
                java(
                    """
                package $ooPackage;
                
                import com.openobserve.android.InternalSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       InternalSdkClass instance = new InternalSdkClass();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal method is used from OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodKotlinStub,
                java(
                    """
                package $ooPackage;
                
                import com.openobserve.android.SomeSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SomeSdkClass instance = new SomeSdkClass();
                       instance.internalMethod();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal property is used from OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalPropertyKotlinStub,
                java(
                    """
                package $ooPackage;
                
                import com.openobserve.android.SomeSdkClass;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SomeSdkClass instance = new SomeSdkClass();
                       instance.getInternalProperty();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal extension method is used from OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalExtensionMethodKotlinStub,
                java(
                    """
                package $ooPackage;
                
                import com.openobserve.android.TestKt;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       TestKt.internalMethod("something");
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal method in object class is used from OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInObjectClassKotlinStub,
                java(
                    """
                package $ooPackage;
                
                import com.openobserve.android.OpenObserve;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       OpenObserve._internalProxy();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W non-int method called from int interface impl from non-OpenObserve package { java }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInInterfaceKotlinStub,
                classImplementingInternalInterfaceKotlinStub,
                java(
                    """
                package $nonOpenObservePackage;
                
                import com.openobserve.android.SdkCore;
                
                public class ClassUnderTest { 
                
                    public void testMethod() {
                       SdkCore instance = new SdkCore();
                       instance.okayMethod();
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal class is used from OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalClassKotlinStub,
                kotlin(
                    """
                package $ooPackage
                
                import com.openobserve.android.InternalSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = InternalSdkClass()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal method is used from OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodKotlinStub,
                kotlin(
                    """
                package $ooPackage
                
                import com.openobserve.android.SomeSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SomeSdkClass()
                       instance.internalMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal property is used from OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalPropertyKotlinStub,
                kotlin(
                    """
                package $ooPackage
                
                import com.openobserve.android.SomeSdkClass
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SomeSdkClass()
                       instance.internalProperty
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal extension method is used from OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalExtensionMethodKotlinStub,
                kotlin(
                    """
                package $ooPackage
                
                import com.openobserve.android.internalMethod
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       "something".internalMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W internal method in object class is used from OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInObjectClassKotlinStub,
                kotlin(
                    """
                package $ooPackage
                
                import com.openobserve.android.OpenObserve
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       OpenObserve._internalProxy()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `M not report issue W non-int method called from int interface impl from non-OpenObserve package { kotlin }`() {
        lint()
            .files(
                internalApiAnnotationStub,
                internalMethodInInterfaceKotlinStub,
                classImplementingInternalInterfaceKotlinStub,
                kotlin(
                    """
                package $nonOpenObservePackage
                
                import com.openobserve.android.SdkCore
                
                class ClassUnderTest { 
                
                    fun testMethod() {
                       val instance = SdkCore()
                       instance.okayMethod()
                    }
                
                }
                    """
                ).indented()
            )
            .issues(InternalApiUsageDetector.ISSUE)
            .run()
            .expectClean()
    }

    companion object {
        val internalApiAnnotationStub: TestFile = kotlin(
            """
                package com.openobserve.android.lint
                
                @Retention(AnnotationRetention.BINARY)
                @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
                annotation class InternalApi
            """
        ).indented()

        val internalClassKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android
                
                import com.openobserve.android.lint.InternalApi;
                
                @InternalApi
                class InternalSdkClass
            """
        ).indented()

        val internalMethodInInterfaceKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android

                import com.openobserve.android.lint.InternalApi;

                interface InternalSdkCore {
                  @InternalApi
                  fun internalMethod()
                }

            """
        ).indented()

        val internalMethodInObjectClassKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android

                import com.openobserve.android.lint.InternalApi;
                import kotlin.jvm.JvmStatic;

                object OpenObserve {

                  @JvmStatic
                  @InternalApi
                  fun _internalProxy() = Unit
                }

            """
        ).indented()

        val classImplementingInternalInterfaceKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android

                class SdkCore : InternalSdkCore {
                    override fun internalMethod() = Unit
                    fun okayMethod() = Unit
                }
            """
        ).indented()

        val internalMethodKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android
                
                import com.openobserve.android.lint.InternalApi;
                
                class SomeSdkClass {   
                  @InternalApi
                  fun internalMethod() = Unit
                }
            """
        ).indented()

        val internalPropertyKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android
                
                import com.openobserve.android.lint.InternalApi;
                
                class SomeSdkClass {   
                  @InternalApi
                  val internalProperty = 42
                }
            """
        ).indented()

        val internalExtensionMethodKotlinStub: TestFile = kotlin(
            """
                package com.openobserve.android
                
                import com.openobserve.android.lint.InternalApi;
                
                @InternalApi
                fun String.internalMethod() = Unit
            """
        ).indented()
    }

    // region private

    private fun String.packageToPath() = replace(".", File.separator)

    // endregion
}
