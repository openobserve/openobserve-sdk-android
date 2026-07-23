# OpenObserve Integration for Coil 3

## Getting Started 

To include the OpenObserve integration for [Coil 3][1] in your project, add the
following to your application's `build.gradle.kts` file.

```kotlin
dependencies {
    implementation("ai.openobserve:o2-sdk-android-rum:<latest-version>")
    implementation("ai.openobserve:o2-sdk-android-okhttp:<latest-version>")
    implementation("ai.openobserve:o2-sdk-android-coil3:<latest-version>")
}
```

### Initial Setup

1. Setup RUM monitoring, see the dedicated [OpenObserve Android RUM Collection documentation][2] to learn how.
2. Setup OkHttp instrumentation with OpenObserve RUM SDK, see the [dedicated documentation][3] to learn how.

Follow Coil 3's [documentation][4] to:
 
 - Create your own `ImageLoader` by providing your own `OkHttpClient` (configured with `OpenObserveInterceptor`) using `OkHttpNetworkFetcherFactory`.

```kotlin
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(OpenObserveInterceptor.Builder(tracedHosts).build())
        .build()

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(okHttpClient))
        }
        .build()

    SingletonImageLoader.setSafe { imageLoader }
```

- Decorate the `ImageRequest.Builder` with the `OpenObserveCoilRequestListener` whenever you perform an image loading request.
 
```kotlin
    imageView.load(uri) {
       listener(OpenObserveCoilRequestListener())
    }
```

With this setup:
- The `OpenObserveInterceptor` on OkHttpClient automatically tracks Coil's network requests (creating APM Traces and RUM Resource events)
- The `OpenObserveCoilRequestListener` reports image loading failures (creating RUM Error events)

## Contributing

For details on contributing, read the 
[Contributing Guide](../../CONTRIBUTING.md).

## License

[Apache License, v2.0](../../LICENSE)

[1]: https://github.com/coil-kt/coil
[2]: https://openobserve.ai/docs/real_user_monitoring/android/?tab=kotlin
[3]: https://openobserve.ai/docs/real_user_monitoring/android/advanced_configuration/?tab=kotlin#automatically-track-network-requests
[4]: https://coil-kt.github.io/coil/upgrading_to_coil3/

