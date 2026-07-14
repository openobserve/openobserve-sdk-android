/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

#ifndef SIGNAL_MONITOR_H
#define SIGNAL_MONITOR_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Monitor for fatal signals, updating the crash context when detected, the
 * serialize to disk and invoke the previously-installed handler
 * @return true if monitoring started successfully
 */
bool start_monitoring();

/**
 * Stop monitoring for fatal exceptions and reinstall previously-installed
 * handlers
 */
void stop_monitoring(void);

#ifdef __cplusplus
}
#endif
#endif