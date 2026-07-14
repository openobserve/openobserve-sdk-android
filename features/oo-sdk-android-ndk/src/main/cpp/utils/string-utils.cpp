/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

#include "string-utils.h"

#include <jni.h>
#include <string>

namespace stringutils {

    std::string copy_to_string(JNIEnv *env, jstring from) {
        if (from == nullptr) {
            return {};
        }

        const char *raw_str = env->GetStringUTFChars(from, nullptr);

        std::string result(raw_str);

        env->ReleaseStringUTFChars(from, raw_str);

        return result;
    }

}
