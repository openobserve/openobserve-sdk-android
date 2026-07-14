/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

#include <string>
#include <list>

namespace testutils {

    std::list<std::string> split_backtrace_into_lines(const char *buffer);
}
