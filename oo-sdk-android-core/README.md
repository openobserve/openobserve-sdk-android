# OpenObserve SDK for Android - core library

## Getting started

To include the OpenObserve SDK for Android in your project, simply add any product you want to use to your application's `build.gradle.kts` file.

For example, in case of RUM:

```kotlin
dependencies {
    implementation("io.openobserve:oo-sdk-android-rum:<latest-version>")
}
```

### Initial Setup

Before you can use the SDK, you need to set up the library with your application
context and your API token. You can create a token from the Integrations > API
in OpenObserve. **Make sure you create a key of type `Client Token`.**

```kotlin
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val configuration = Configuration.Builder(
            clientToken = CLIENT_TOKEN,
            env = ENV_NAME,
            variant = APP_VARIANT_NAME
        )
            .useSite(OpenObserveSite.US1) // replace with the site you're targeting (e.g.: US3, EU1, …)
            .build()
        OpenObserve.initialize(this, configuration, trackingConsent)
    }
}
```

### Using a secondary instance of the SDK

It is possible to initialize multiple instances of the SDK by associating them with a name. Many methods of the SDK can optionally take an SDK instance as an argument. If not provided, the call is associated with the default (nameless) SDK instance.

Here is an example illustrating how to initialize a secondary core instance and use it:

```kotlin
val namedSdkInstance = OpenObserve.initialize("myInstance", context, configuration, trackingConsent)
val userInfo = UserInfo(...)
OpenObserve.setUserInfo(userInfo, sdkCore = namedSdkInstance)
```

**Note**: The SDK instance name should have the same value between application runs. Storage paths for SDK events are associated with it.

You can retrieve the named SDK instance by calling `OpenObserve.getInstance(<name>)` and use the `OpenObserve.isInitialized(<name>)` method to check if the particular SDK instance is initialized.

## Setting up OpenObserve RUM SDK

See the dedicated [OpenObserve Android RUM Collection documentation][1] to learn how to send RUM data from your Android or Android TV application to OpenObserve.

## Setting up the OpenObserve Logs SDK

See the dedicated [OpenObserve Android Log Collection documentation][2] to learn how to forward logs from your Android or Android TV application to OpenObserve.

## Setting up OpenObserve Trace SDK

See the dedicated [OpenObserve Android Trace Collection documentation][3] to learn how to send traces from your Android or Android TV application to OpenObserve.

## Setting the Library's verbosity

If you need to get information about the Library, you can set the verbosity
level as follows: 

```kotlin
    OpenObserve.setVerbosity(Log.INFO)
```

All the internal messages in the library with a priority equal or higher than
the provided level will be logged to Android's LogCat.

## Contributing

Pull requests are welcome, but please open an issue first to discuss what you
would like to change. For more information, read the 
[Contributing Guide](../CONTRIBUTING.md).

## License

[Apache License, v2.0](../LICENSE)

[1]: https://openobserve.ai/docs/real_user_monitoring/android/?tab=kotlin
[2]: https://openobserve.ai/docs/logs/log_collection/android/?tab=kotlin
[3]: https://openobserve.ai/docs/tracing/trace_collection/dd_libraries/android/?tab=kotlin
