/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

#include "datetime-utils.h"

#include <chrono>

uint64_t time_since_epoch() {
    using namespace std::chrono;
    return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
}

void format_date(char *buffer, size_t buffer_size, const char *format) {
    using namespace std::chrono;
    const std::time_t t = time(nullptr);
    const tm *timeinfo = gmtime(&t);
    strftime(buffer, buffer_size, format, timeinfo);
}