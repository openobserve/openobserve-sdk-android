# OpenObserve benchmark module

## Overview

This is a module for OpenObserve internal use to benchmark the overhead of SDK features.
It measures gauges that include CPU ticks, memory usage, and FPS of the application.

## Getting started

1. Build the OpenObserve exporter configuration:

```kotlin
val configuration = OpenObserveExporterConfiguration.Builder("API_KEY")
    .setApplicationId("YOUR_APPLICATION_ID")
    .setApplicationName("Benchmark Application")
    .setApplicationVersion(BuildConfig.VERSION_NAME)
    .setIntervalInSeconds(30L)
    .build()

```

2. Create `OpenObserveMeter` with the configuration:

``` kotlin
val meter = OpenObserveMeter.create(configuration)
```

3. Start all gauges with meter:

```kotlin
meter.startGauges()
```

4. Stop all gauges with meter:

```kotlin
meter.stopGauges()
```

## Observe Metrics

All the metrics are uploaded
through the [OpenObserve metric submission API](https://openobserve.ai/docs/api/latest/metrics/#submit-metrics).
The metric names for each gauge:

* [android.benchmark.cpu]: CPU ticks
* [android.benchmark.memory]: Memory usage
* [android.benchmark.fps]: Frames per second
