/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

#include "file-utils.h"

#include <cerrno>
#include <dirent.h>
#include <sys/stat.h>

namespace fileutils {

    bool create_dir_if_not_exists(const char *dirPath) {
        if (opendir(dirPath) == nullptr && ENOENT == errno) {
            // directory does not exist. We will create it.
            return mkdir(dirPath, S_IRWXU) == 0;
        }

        return true; // the directory was already there
    }
}

